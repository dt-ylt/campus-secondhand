package com.campus.secondhand.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 公共配置类
 *
 * @Configuration：告诉 Spring 这是一个配置类，里面的 @Bean 方法返回的对象会被注册成 Spring Bean
 */
@Configuration
public class CommonConfig {

    /**
     * 密码加密器
     * 用 BCrypt 算法。注册时把明文密码加密后存库，登录时用它比对。
     * @Bean：把这个对象注册成 Spring Bean，之后 UserService 里可以 @Autowired 注入使用
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
