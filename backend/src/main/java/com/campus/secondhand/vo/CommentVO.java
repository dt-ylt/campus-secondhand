package com.campus.secondhand.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 留言返回对象：留言内容 + 谁留的言（昵称/头像）
 */
@Data
public class CommentVO {

    private Long id;

    private Long productId;

    private String content;

    private Long userId;

    /** 留言人昵称 */
    private String nickname;

    /** 留言人头像 */
    private String avatar;

    private LocalDateTime createTime;
}
