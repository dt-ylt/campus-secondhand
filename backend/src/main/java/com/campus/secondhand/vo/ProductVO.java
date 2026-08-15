package com.campus.secondhand.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品详情返回对象
 * 在 Product 实体基础上，额外拼上分类名、卖家昵称、图片列表，前端显示更方便
 */
@Data
public class ProductVO {

    private Long id;

    private String title;

    private String description;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Long categoryId;

    /** 分类名（如"数码电子"），拼给前端直接显示 */
    private String categoryName;

    private Integer conditionLevel;

    private String location;

    /** 状态：0待审核 1已上架 2已下架 3已售出 */
    private Integer status;

    private Integer viewCount;

    /** 发布者用户ID */
    private Long userId;

    /** 卖家昵称 */
    private String sellerName;

    /** 图片URL列表，第一张是封面 */
    private List<String> images;

    private LocalDateTime createTime;
}
