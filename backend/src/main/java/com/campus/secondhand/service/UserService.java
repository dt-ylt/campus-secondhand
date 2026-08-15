package com.campus.secondhand.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.secondhand.dto.LoginDTO;
import com.campus.secondhand.dto.RegisterDTO;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.vo.LoginVO;

/**
 * 用户业务层接口
 *
 * extends IService<User>：MyBatis-Plus 的 Service 模式，继承后白嫖一堆通用方法
 * （save、getById、list 等），自己只需定义业务方法。
 */
public interface UserService extends IService<User> {

    /** 注册 */
    void register(RegisterDTO dto);

    /** 登录，返回 token + 用户信息 */
    LoginVO login(LoginDTO dto);

    /** 按ID查用户（获取个人信息用） */
    User getUserById(Long id);

    /** 管理员查用户列表 */
    IPage<User> adminPage(Integer pageNum, Integer pageSize);

    /** 管理员封禁/解禁用户（0禁用 1正常） */
    void updateStatus(Long userId, Integer status);
}
