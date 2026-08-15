package com.campus.secondhand.interceptor;

import com.campus.secondhand.common.exception.BusinessException;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理员权限拦截器：只拦 /admin/** 路径
 *
 * 和 JwtInterceptor 的分工（两层门卫）：
 *   JwtInterceptor：验"你是谁"（token -> userId，存在 request 属性里）
 *   AdminInterceptor：验"你是不是管理员"（查库看 role 是否为 1）
 *
 * 拦截器按注册顺序执行：先过 JwtInterceptor（拿到 userId），再到这里查角色。
 * 注意：这个拦截器只对 /admin/** 注册，普通接口不受影响。
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 拿 JwtInterceptor 存进来的 userId（到这来说明已经过登录校验，这里再防御一下）
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }

        // 2. 查库判断角色。为什么不用 JWT 里带角色？JWT 签发后改不了，
        //    如果管理员被撤职，旧 token 里的角色还是 1，直到过期。查库最准。
        User user = userMapper.selectById(userId);
        if (user == null || user.getRole() == null || user.getRole() != 1) {
            throw new BusinessException(403, "无管理员权限");
        }

        return true;   // 是管理员，放行
    }
}
