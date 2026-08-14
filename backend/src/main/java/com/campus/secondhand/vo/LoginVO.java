package com.campus.secondhand.vo;

import lombok.Data;

/**
 * 登录成功返回对象
 * 包含 token（前端后续请求带上它证明身份）+ 用户基本信息。
 * 故意不含 password，避免密码泄露。
 */
@Data
public class LoginVO {

    /** JWT 通行证 */
    private String token;

    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    /** 角色：0普通用户，1管理员。前端据此显示不同界面 */
    private Integer role;
}
