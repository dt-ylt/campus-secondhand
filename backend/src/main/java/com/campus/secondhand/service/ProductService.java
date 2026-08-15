package com.campus.secondhand.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.secondhand.dto.ProductPublishDTO;
import com.campus.secondhand.dto.ProductQueryDTO;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.vo.ProductVO;

import java.util.List;

/**
 * 商品业务层接口
 */
public interface ProductService extends IService<Product> {

    /** 发布商品 */
    void publish(Long userId, ProductPublishDTO dto);

    /** 编辑商品（只能编辑自己发布的） */
    void update(Long userId, Long productId, ProductPublishDTO dto);

    /** 删除商品（逻辑删除，只能删自己发布的） */
    void delete(Long userId, Long productId);

    /** 商品详情（拼装分类名、卖家、图片列表） */
    ProductVO getDetail(Long productId);

    /** 分页 + 条件筛选查询商品列表（只查已上架的） */
    IPage<ProductVO> pageQuery(ProductQueryDTO dto);

    /** 我发布的商品列表（含待审核/已下架，管理自己的闲置用） */
    List<ProductVO> myProducts(Long userId);

    /** 按ID批量查商品并组装VO（收藏列表等场景复用） */
    List<ProductVO> listVOByIds(List<Long> productIds);
}
