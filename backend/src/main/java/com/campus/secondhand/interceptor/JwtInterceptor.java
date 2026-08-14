package com.campus.secondhand.interceptor;

import com.campus.secondhand.common.exception.BusinessException;
import com.campus.secondhand.common.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 拦截器（登录鉴权关卡）
 *
 * 每个被拦截的请求，先到这里：
 *   有有效 token -> 取出 userId 存到 request 属性，放行
 *   没有/无效 token -> 抛 401 异常(被全局异常处理器接住，返回"未登录")
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * preHandle：请求到达 Controller 之前执行
     * 返回 true 放行，返回 false 或抛异常则拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 从请求头取 token（前端约定放在 Authorization 头，格式 "Bearer xxx"）
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);   // 去掉 "Bearer " 前缀，只留 token 本体
        }

        // 2. 没 token 或 token 无效 -> 抛 401
        if (token == null || token.isEmpty() || !jwtUtils.validateToken(token)) {
            throw new BusinessException(401, "未登录或登录已过期");
        }

        // 3. token 有效，取出 userId 存进 request 属性，Controller 用 @RequestAttribute 取
        Long userId = jwtUtils.getUserIdFromToken(token);
        request.setAttribute("userId", userId);

        return true;   // 放行
    }
}
