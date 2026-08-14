package com.campus.secondhand.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 注册请求参数
 *
 * 为什么不直接用 User 接收？因为注册只该传 username/password/nickname，
 * 不能让前端传 role/id 这些敏感字段（否则有人传 role=1 就成管理员了）。
 * DTO 只接收该接收的字段，隔离外部输入和内部实体。
 *
 * @NotBlank/@Size：参数校验注解，校验失败会被全局异常处理器接住，自动返回提示信息。
 */
@Data
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度需在3-20个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度需在6-20个字符之间")
    private String password;

    /** 昵称，选填 */
    private String nickname;
}
