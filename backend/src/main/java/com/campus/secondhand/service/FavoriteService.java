package com.campus.secondhand.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.secondhand.entity.Favorite;
import com.campus.secondhand.vo.ProductVO;

import java.util.List;

/**
 * 收藏业务层接口
 */
public interface FavoriteService extends IService<Favorite> {

    /** 收藏商品 */
    void favorite(Long userId, Long productId);

    /** 取消收藏 */
    void unfavorite(Long userId, Long productId);

    /** 我的收藏列表（返回商品信息） */
    List<ProductVO> myFavorites(Long userId);
}
