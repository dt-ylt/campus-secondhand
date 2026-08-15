package com.campus.secondhand.config;

import com.campus.secondhand.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：注册 JWT 拦截器
 *
 * 拦截所有请求，但排除"不需要登录就能访问"的接口：
 * - 注册/登录（本来就没登录）
 * - 商品列表/商品详情/分类列表（浏览商品不应强制登录，就像逛淘宝不用先登录）
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/register",
                        "/user/login",
                        "/product/list",
                        "/product/{id}",
                        "/category/list"
                );
    }
}
