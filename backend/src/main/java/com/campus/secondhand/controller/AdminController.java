package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.secondhand.common.result.Result;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.service.ProductService;
import com.campus.secondhand.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员后台接口（全部在 /admin/** 下，由 AdminInterceptor 统一校验管理员身份）
 *
 * 注意：管理接口用查询参数传 id（?productId=1），不用路径占位符，简单直接好测试。
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    /**
     * 商品列表（管理员视角，能看所有状态）
     * GET /admin/product/list?status=0&pageNum=1&pageSize=10
     * status 不传=全部；0待审核 1已上架 2已下架 3已售出（审核页一般传0）
     */
    @GetMapping("/product/list")
    public Result<IPage<Product>> productList(@RequestParam(required = false) Integer status,
                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(productService.adminPage(pageNum, pageSize, status));
    }

    /**
     * 商品审核：上架/下架
     * PUT /admin/product/audit?productId=1&status=1   （1=上架通过，2=下架）
     */
    @PutMapping("/product/audit")
    public Result<Void> audit(@RequestParam Long productId,
                              @RequestParam Integer status) {
        productService.audit(productId, status);
        return Result.success();
    }

    /**
     * 用户列表
     * GET /admin/user/list?pageNum=1&pageSize=10
     * （User 实体的 password 字段有 @JsonIgnore，不会泄露密码）
     */
    @GetMapping("/user/list")
    public Result<IPage<User>> userList(@RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(userService.adminPage(pageNum, pageSize));
    }

    /**
     * 封禁/解禁用户
     * PUT /admin/user/status?userId=2&status=0   （0=封禁，1=恢复正常）
     */
    @PutMapping("/user/status")
    public Result<Void> userStatus(@RequestParam Long userId,
                                   @RequestParam Integer status) {
        userService.updateStatus(userId, status);
        return Result.success();
    }
}
