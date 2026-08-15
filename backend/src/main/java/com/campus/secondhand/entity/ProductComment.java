package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品留言实体类，对应 product_comment 表
 * parent_id 预留了"回复留言"功能（0=顶级留言），本项目先只做顶级留言
 */
@Data
@TableName("product_comment")
public class ProductComment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    private Long userId;

    /** 留言内容 */
    private String content;

    /** 父留言ID，0表示顶级留言（预留回复功能） */
    private Long parentId;

    private LocalDateTime createTime;
}
