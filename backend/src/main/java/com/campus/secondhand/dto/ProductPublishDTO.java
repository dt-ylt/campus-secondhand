package com.campus.secondhand.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * 发布/编辑商品的请求参数
 *
 * 图片流转方式：前端先调 POST /file/upload 把图片传到 MinIO 拿到 URL 列表，
 * 再把 URL 列表放进这个 DTO 一起提交。
 */
@Data
public class ProductPublishDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题最多100字")
    private String title;

    /** 商品详情描述 */
    private String description;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    /** 原价，选填 */
    private BigDecimal originalPrice;

    @NotNull(message = "必须选择分类")
    private Long categoryId;

    /** 成色：1全新 2几乎全新 3轻微使用痕迹 4明显使用痕迹。不传默认1(全新) */
    private Integer conditionLevel;

    /** 交易地点/校区 */
    private String location;

    /** 图片URL列表（第一张是封面） */
    private List<String> images;
}
