package com.campus.secondhand.controller;

import com.campus.secondhand.common.result.Result;
import com.campus.secondhand.dto.LoginDTO;
import com.campus.secondhand.dto.RegisterDTO;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.service.UserService;
import com.campus.secondhand.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户接口
 *
 * @RestController = @Controller + @ResponseBody：返回的对象自动转 JSON
 * @RequestMapping("/user")：这个类下所有接口地址都以 /user 开头
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 注册  POST /user/register
     * @RequestBody：把请求体(JSON)转成 RegisterDTO 对象
     * @Valid：触发 RegisterDTO 上的校验注解(@NotBlank/@Size)
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();   // 注册成功，无数据返回
    }

    /**
     * 登录  POST /user/login
     * 返回 token + 用户信息
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    /**
     * 获取当前登录用户信息  GET /user/info
     * @RequestAttribute("userId")：取拦截器存进来的 userId（所以这接口必须先登录）
     */
    @GetMapping("/info")
    public Result<User> info(@RequestAttribute("userId") Long userId) {
        return Result.success(userService.getUserById(userId));
    }
}
