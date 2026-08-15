package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 闲置商品实体类，对应 product 表（核心业务表）
 */
@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发布者用户ID */
    private Long userId;

    /** 商品标题 */
    private String title;

    /** 详情描述 */
    private String description;

    /** 售价。数据库 DECIMAL 对应 Java 的 BigDecimal（精确存钱，不用 double） */
    private BigDecimal price;

    /** 原价，可空 */
    private BigDecimal originalPrice;

    /** 所属分类ID */
    private Long categoryId;

    /** 成色：1全新 2几乎全新 3轻微使用痕迹 4明显使用痕迹 */
    private Integer conditionLevel;

    /** 交易地点/校区 */
    private String location;

    /** 状态：0待审核 1已上架 2已下架 3已售出 */
    private Integer status;

    /** 浏览量（查看详情时+1） */
    private Integer viewCount;

    /**
     * 逻辑删除标记：0未删除 1已删除
     * @TableLogic：调用 deleteById 实际执行 UPDATE SET deleted=1，查询自动加 WHERE deleted=0
     */
    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
