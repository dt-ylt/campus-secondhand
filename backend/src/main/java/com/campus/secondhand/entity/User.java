package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类，对应数据库 sys_user 表
 *
 * @TableName("sys_user")：指定表名（类名 User 和表名 sys_user 不一致，所以要指定）
 * @Data：Lombok 生成 getter/setter
 * @TableId：标记主键，type=AUTO 表示数据库自增
 *
 * 字段名自动映射：Java 的 createTime 对应数据库的 create_time
 * （下划线转驼峰，application.yml 里的 map-underscore-to-camel-case 配的）
 */
@Data
@TableName("sys_user")
public class User {

    /** 用户ID，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号 */
    private String username;

    /** 密码（BCrypt 加密后的密文）。@JsonIgnore：不让它出现在接口返回的 JSON 里，防止泄露 */
    @JsonIgnore
    private String password;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 手机号 */
    private String phone;

    /** 角色：0普通用户，1管理员 */
    private Integer role;

    /** 状态：1正常，0禁用 */
    private Integer status;

    /** 注册时间（数据库默认填当前时间） */
    private LocalDateTime createTime;

    /** 更新时间（数据库修改时自动刷新） */
    private LocalDateTime updateTime;
}
