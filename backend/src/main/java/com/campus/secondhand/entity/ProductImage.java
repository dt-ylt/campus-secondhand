package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品图片实体类，对应 product_image 表
 * 一个商品有多张图，每张图一行记录，url 存 MinIO 返回的地址
 */
@Data
@TableName("product_image")
public class ProductImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属商品ID */
    private Long productId;

    /** 图片URL（MinIO 返回的地址） */
    private String url;

    /** 排序，控制图片展示顺序（第几张图） */
    private Integer sort;

    private LocalDateTime createTime;
}
