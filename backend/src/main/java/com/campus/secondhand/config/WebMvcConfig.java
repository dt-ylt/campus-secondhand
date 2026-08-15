package com.campus.secondhand.config;

import com.campus.secondhand.interceptor.AdminInterceptor;
import com.campus.secondhand.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：注册拦截器（注意注册顺序 = 执行顺序）
 *
 * 1. JwtInterceptor：拦所有请求，验证登录（放行注册/登录/浏览类接口）
 * 2. AdminInterceptor：只拦 /admin/**，在登录校验之后验证管理员角色
 *
 * ⚠️ 放行路径必须精确到"段"，不能用 /product/{id} 这种宽泛通配：
 *    它会连 /product/publish、/product/my 一起匹配，导致这些本该登录的接口被放行。
 *    所以商品详情也不在放行列表里（看详情需登录），只有列表/分类/留言列表公开。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 先注册的先执行：第一层，登录校验
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/register",
                        "/user/login",
                        "/product/list",
                        "/category/list",
                        "/comment/list/{productId}"
                );

        // 第二层：管理员校验（只拦 /admin/**，此时 userId 已由第一层存好）
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**");
    }
}
