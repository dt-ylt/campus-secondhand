package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.secondhand.common.exception.BusinessException;
import com.campus.secondhand.common.utils.JwtUtils;
import com.campus.secondhand.dto.LoginDTO;
import com.campus.secondhand.dto.RegisterDTO;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.UserService;
import com.campus.secondhand.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户业务层实现
 *
 * @Service：标记为 Service 层 Bean，交给 Spring 管理
 * extends ServiceImpl<UserMapper, User>：继承后自带一个 baseMapper 字段(就是 UserMapper)，可直接用
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;   // 密码加密器（CommonConfig 里定义的 Bean）

    @Autowired
    private JwtUtils jwtUtils;                  // JWT 工具类

    /**
     * 注册：查重 -> 加密密码 -> 插入
     */
    @Override
    public void register(RegisterDTO dto) {
        // 1. 查重：按用户名查，已存在就抛业务异常（会被全局异常处理器接住）
        User existing = baseMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 构造用户对象，密码用 BCrypt 加密后存
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        // 昵称没填就用用户名
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setRole(0);     // 普通用户
        user.setStatus(1);   // 正常状态

        // 3. 插入数据库
        baseMapper.insert(user);
    }

    /**
     * 登录：查用户 -> 校验密码 -> 校验状态 -> 生成token
     */
    @Override
    public LoginVO login(LoginDTO dto) {
        // 1. 按用户名查用户
        User user = baseMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            // 注意：用户不存在和密码错都返回同一句"用户名或密码错误"，不告诉攻击者用户名是否存在（防用户枚举）
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 校验密码：matches(明文, 密文)，BCrypt 内部会处理盐
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 校验账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 4. 生成 JWT token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());

        // 5. 组装返回对象（不含密码）
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        return vo;
    }

    /**
     * 按ID查用户（获取个人信息）
     */
    @Override
    public User getUserById(Long id) {
        User user = baseMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }
}
