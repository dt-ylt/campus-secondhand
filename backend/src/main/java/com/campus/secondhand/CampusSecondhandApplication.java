package com.campus.secondhand;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园二手交易平台 - 后端启动类
 *
 * 一个 Spring Boot 项目只需要这一个带 main 方法的类就能启动。
 * @SpringBootApplication 是个组合注解，相当于同时加了三个注解：
 *   - @SpringBootConfiguration：标记这是一个配置类
 *   - @EnableAutoConfiguration：开启自动配置（Spring Boot 的核心魔法，根据依赖自动装配）
 *   - @ComponentScan：扫描当前包(com.campus.secondhand)及其子包下的所有组件
 *                     所以后面的 Controller/Service/Mapper 都要写在这个包或子包下，才能被扫到
 *
 * @MapperScan：扫描 mapper 包下的所有 Mapper 接口，让 MyBatis-Plus 给它们生成实现类并注册成 Bean。
 *              这样每个 Mapper 就不用单独加 @Mapper 注解了。
 */
@MapperScan("com.campus.secondhand.mapper")
@SpringBootApplication
public class CampusSecondhandApplication {

    /**
     * 程序入口：运行这个 main 方法就能启动整个后端
     * SpringApplication.run 会启动内嵌的 Tomcat 服务器，监听端口(默认8080)
     */
    public static void main(String[] args) {
        SpringApplication.run(CampusSecondhandApplication.class, args);
    }
}
