package com.campus.secondhand.config;

import com.campus.secondhand.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：注册拦截器
 *
 * 拦截所有请求，但把"注册""登录"排除掉（这俩接口本来就还没登录，不能拦）。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")                            // 拦截所有请求
                .excludePathPatterns("/user/register", "/user/login");  // 注册、登录放行
    }
}
