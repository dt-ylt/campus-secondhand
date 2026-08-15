package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.secondhand.common.exception.BusinessException;
import com.campus.secondhand.dto.ProductPublishDTO;
import com.campus.secondhand.dto.ProductQueryDTO;
import com.campus.secondhand.entity.Category;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.entity.ProductImage;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.mapper.CategoryMapper;
import com.campus.secondhand.mapper.ProductImageMapper;
import com.campus.secondhand.mapper.ProductMapper;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.ProductService;
import com.campus.secondhand.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品业务实现
 *
 * @Transactional：事务。发布商品要插 product 表 + 插多条 product_image 表，
 * 如果中途失败，已插入的全部回滚，避免出现"有商品没图"的脏数据。
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 发布商品
     */
    @Override
    @Transactional
    public void publish(Long userId, ProductPublishDTO dto) {
        // 1. 校验分类存在
        Category category = categoryMapper.selectById(dto.getCategoryId());
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 2. 插入商品（状态0=待审核，管理员审核通过后才上架）
        Product product = new Product();
        product.setUserId(userId);
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setOriginalPrice(dto.getOriginalPrice());
        product.setCategoryId(dto.getCategoryId());
        product.setConditionLevel(dto.getConditionLevel() != null ? dto.getConditionLevel() : 1);
        product.setLocation(dto.getLocation());
        product.setStatus(0);
        product.setViewCount(0);
        productMapper.insert(product);   // 插入后自增ID会自动回填到 product.getId()

        // 3. 保存图片列表
        saveImages(product.getId(), dto.getImages());
    }

    /**
     * 编辑商品
     */
    @Override
    @Transactional
    public void update(Long userId, Long productId, ProductPublishDTO dto) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        // 权限校验：只能编辑自己发布的商品（防止改别人的）
        if (!product.getUserId().equals(userId)) {
            throw new BusinessException(403, "只能编辑自己发布的商品");
        }

        Category category = categoryMapper.selectById(dto.getCategoryId());
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setOriginalPrice(dto.getOriginalPrice());
        product.setCategoryId(dto.getCategoryId());
        product.setConditionLevel(dto.getConditionLevel() != null ? dto.getConditionLevel() : 1);
        product.setLocation(dto.getLocation());
        product.setStatus(0);   // 编辑后重新进入待审核，防止有人过审后偷改内容
        productMapper.updateById(product);

        // 图片策略：删掉旧图片记录，插入新列表（简单直接）
        productImageMapper.delete(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, productId));
        saveImages(productId, dto.getImages());
    }

    /**
     * 删除商品（逻辑删除）
     */
    @Override
    public void delete(Long userId, Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (!product.getUserId().equals(userId)) {
            throw new BusinessException(403, "只能删除自己发布的商品");
        }
        // @TableLogic 生效：实际执行 UPDATE product SET deleted=1 WHERE id=?
        productMapper.deleteById(productId);
        // 图片记录一并清掉（MinIO 里的文件保留，不影响）
        productImageMapper.delete(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, productId));
    }

    /**
     * 商品详情：把商品、分类、卖家、图片拼成一个 VO 返回
     */
    @Override
    public ProductVO getDetail(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);   // 同名字段自动复制，省得一个个 set

        // 拼分类名
        Category category = categoryMapper.selectById(product.getCategoryId());
        vo.setCategoryName(category != null ? category.getName() : null);

        // 拼卖家昵称
        User seller = userMapper.selectById(product.getUserId());
        vo.setSellerName(seller != null ? seller.getNickname() : null);

        // 拼图片URL列表（按 sort 升序，第一张是封面）
        List<ProductImage> images = productImageMapper.selectList(
                new LambdaQueryWrapper<ProductImage>()
                        .eq(ProductImage::getProductId, productId)
                        .orderByAsc(ProductImage::getSort));
        vo.setImages(images.stream().map(ProductImage::getUrl).collect(Collectors.toList()));

        // 浏览量 +1
        product.setViewCount(product.getViewCount() + 1);
        productMapper.updateById(product);

        return vo;
    }

    /**
     * 分页 + 条件筛选查询商品列表（首页用，只查已上架的）
     */
    @Override
    public IPage<ProductVO> pageQuery(ProductQueryDTO dto) {
        // 1. 拼查询条件。eq/ge/le/like 第一个参数是 boolean：
        //    条件为 true 才把这个筛选拼进 SQL，为 false 就跳过 -> 实现"不传就不筛"
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .eq(dto.getCategoryId() != null, Product::getCategoryId, dto.getCategoryId())
                .ge(dto.getMinPrice() != null, Product::getPrice, dto.getMinPrice())
                .le(dto.getMaxPrice() != null, Product::getPrice, dto.getMaxPrice())
                .like(dto.getKeyword() != null && !dto.getKeyword().isEmpty(),
                        Product::getTitle, dto.getKeyword());

        // 2. 排序：默认按发布时间倒序（最新在前），支持价格升降序切换
        if ("priceAsc".equals(dto.getOrderBy())) {
            wrapper.orderByAsc(Product::getPrice);
        } else if ("priceDesc".equals(dto.getOrderBy())) {
            wrapper.orderByDesc(Product::getPrice);
        } else {
            wrapper.orderByDesc(Product::getCreateTime);
        }

        // 3. 分页查询（LIMIT 由分页插件自动拼）
        Page<Product> productPage = productMapper.selectPage(
                new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);

        // 4. 把查出来的商品列表组装成 VO 列表
        List<ProductVO> voList = assembleVOList(productPage.getRecords());

        // 5. 用 VO 列表 + 原分页信息(total等)组装返回
        Page<ProductVO> voPage = new Page<>(productPage.getCurrent(), productPage.getSize(), productPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 我发布的商品列表（含待审核/已下架）
     */
    @Override
    public List<ProductVO> myProducts(Long userId) {
        List<Product> products = productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getUserId, userId)
                        .orderByDesc(Product::getCreateTime));
        return assembleVOList(products);
    }

    /**
     * 按ID批量查商品并组装VO（收藏列表等场景复用）
     */
    @Override
    public List<ProductVO> listVOByIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Product> products = productMapper.selectBatchIds(productIds);
        return assembleVOList(products);
    }

    /**
     * 管理员查商品列表：和首页列表的区别是不强制 status=1，能看所有状态
     */
    @Override
    public IPage<Product> adminPage(Integer pageNum, Integer pageSize, Integer status) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        // status 不传就查全部状态；传了就只看那种状态（审核页一般传 0 待审核）
        wrapper.eq(status != null, Product::getStatus, status)
               .orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 管理员审核商品：改状态（1上架 / 2下架）
     */
    @Override
    public void audit(Long productId, Integer status) {
        if (status == null || (status != 1 && status != 2)) {
            throw new BusinessException("审核操作只能是 1(上架) 或 2(下架)");
        }
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        product.setStatus(status);
        productMapper.updateById(product);
    }

    /**
     * 把商品列表组装成 VO 列表（分类名、卖家昵称、图片批量拼装）
     *
     * 性能要点：批量查询，避免 N+1 问题。
     * 笨办法是循环里每件商品查一次图片/分类/卖家（10件商品=30次查询）；
     * 这里改成先收集所有ID，用 IN 一次查回来，再内存里对号入座（总共3次查询）。
     * 面试高频："列表接口怎么避免N+1查询"就这么答。
     */
    private List<ProductVO> assembleVOList(List<Product> products) {
        List<ProductVO> voList = new ArrayList<>();
        if (products == null || products.isEmpty()) {
            return voList;
        }

        // 1. 批量查图片：WHERE product_id IN (...)，按 productId 分组
        List<Long> productIds = products.stream().map(Product::getId).collect(Collectors.toList());
        Map<Long, List<String>> imageMap = productImageMapper.selectList(
                        new LambdaQueryWrapper<ProductImage>()
                                .in(ProductImage::getProductId, productIds)
                                .orderByAsc(ProductImage::getSort))
                .stream()
                .collect(Collectors.groupingBy(ProductImage::getProductId,
                        Collectors.mapping(ProductImage::getUrl, Collectors.toList())));

        // 2. 批量查分类（分类总共就几个，直接全查，转成 Map<id, name>）
        Map<Long, String> categoryMap = categoryMapper.selectList(null).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        // 3. 批量查卖家昵称
        Set<Long> userIds = products.stream().map(Product::getUserId).collect(Collectors.toSet());
        Map<Long, String> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getNickname));

        // 4. 逐件组装（纯内存操作，不再查库）
        for (Product p : products) {
            ProductVO vo = new ProductVO();
            BeanUtils.copyProperties(p, vo);
            vo.setCategoryName(categoryMap.get(p.getCategoryId()));
            vo.setSellerName(userMap.get(p.getUserId()));
            vo.setImages(imageMap.getOrDefault(p.getId(), Collections.emptyList()));
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 批量保存商品图片
     */
    private void saveImages(Long productId, List<String> images) {
        if (images == null || images.isEmpty()) {
            return;
        }
        int sort = 0;
        for (String url : images) {
            ProductImage img = new ProductImage();
            img.setProductId(productId);
            img.setUrl(url);
            img.setSort(sort++);
            productImageMapper.insert(img);
        }
    }
}
