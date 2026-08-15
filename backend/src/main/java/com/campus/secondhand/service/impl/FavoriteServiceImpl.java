package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.secondhand.common.exception.BusinessException;
import com.campus.secondhand.entity.Favorite;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.mapper.FavoriteMapper;
import com.campus.secondhand.service.FavoriteService;
import com.campus.secondhand.service.ProductService;
import com.campus.secondhand.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏业务实现
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private ProductService productService;

    /**
     * 收藏商品
     */
    @Override
    public void favorite(Long userId, Long productId) {
        // 1. 商品要存在
        Product product = productService.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 2. 是否已收藏过（业务层先查一次给友好提示）
        Long count = favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId));
        if (count > 0) {
            throw new BusinessException("已收藏过该商品");
        }

        // 3. 插入收藏记录
        // 即使并发时两个人同时点收藏穿透了上面的检查，
        // 表上的 (user_id, product_id) 联合唯一索引也会让第二条插入直接报错兜底
        Favorite f = new Favorite();
        f.setUserId(userId);
        f.setProductId(productId);
        favoriteMapper.insert(f);
    }

    /**
     * 取消收藏：删记录（物理删除，收藏取消就该真删，不用逻辑删除）
     */
    @Override
    public void unfavorite(Long userId, Long productId) {
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId));
    }

    /**
     * 我的收藏：先查收藏记录拿商品ID，再批量查商品组装VO
     */
    @Override
    public List<ProductVO> myFavorites(Long userId) {
        List<Favorite> favorites = favoriteMapper.selectList(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreateTime));
        if (favorites.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> productIds = favorites.stream()
                .map(Favorite::getProductId)
                .collect(Collectors.toList());
        // 复用商品模块的批量组装逻辑（阶段4写的 assembleVOList，避免重复代码）
        return productService.listVOByIds(productIds);
    }
}
