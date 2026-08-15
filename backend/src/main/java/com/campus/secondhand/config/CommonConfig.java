package com.campus.secondhand.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 公共配置类
 *
 * ⚠️ 重点：MybatisPlusInterceptor（分页插件）
 * MyBatis-Plus 的 selectPage 分页查询，必须配了这个拦截器才会真正在 SQL 里
 * 拼 LIMIT ?,?。不配的话 SQL 不带 LIMIT，一次查出全表，分页等于失效。
 * 这是 MyBatis-Plus 最经典的坑，面试也常问！
 */
@Configuration
public class CommonConfig {

    /**
     * 密码加密器：BCrypt 算法，注册时加密存库、登录时比对
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * MyBatis-Plus 分页插件：指定数据库类型为 MySQL
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
