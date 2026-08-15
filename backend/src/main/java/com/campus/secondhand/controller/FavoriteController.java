package com.campus.secondhand.controller;

import com.campus.secondhand.common.result.Result;
import com.campus.secondhand.service.FavoriteService;
import com.campus.secondhand.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏接口（都需要登录）
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /** 收藏商品  POST /favorite/{productId} */
    @PostMapping("/{productId}")
    public Result<Void> favorite(@RequestAttribute("userId") Long userId,
                                 @PathVariable Long productId) {
        favoriteService.favorite(userId, productId);
        return Result.success();
    }

    /** 取消收藏  DELETE /favorite/{productId} */
    @DeleteMapping("/{productId}")
    public Result<Void> unfavorite(@RequestAttribute("userId") Long userId,
                                   @PathVariable Long productId) {
        favoriteService.unfavorite(userId, productId);
        return Result.success();
    }

    /** 我的收藏列表  GET /favorite/my */
    @GetMapping("/my")
    public Result<List<ProductVO>> my(@RequestAttribute("userId") Long userId) {
        return Result.success(favoriteService.myFavorites(userId));
    }
}
