package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品分类实体类，对应 category 表
 */
@Data
@TableName("category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类名称，如 数码电子 */
    private String name;

    /** 分类图标URL */
    private String icon;

    /** 排序值，越小越靠前 */
    private Integer sort;

    private LocalDateTime createTime;
}
