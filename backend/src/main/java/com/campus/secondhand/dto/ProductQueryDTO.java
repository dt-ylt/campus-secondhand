package com.campus.secondhand.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品列表查询条件
 * GET /product/list?pageNum=1&pageSize=10&categoryId=2&minPrice=10&maxPrice=100&keyword=键盘
 * Spring 会自动把 URL 上的同名参数绑定到这些字段
 */
@Data
public class ProductQueryDTO {

    /** 页码，默认第1页 */
    private Integer pageNum = 1;

    /** 每页条数，默认10条 */
    private Integer pageSize = 10;

    /** 分类筛选，不传=查所有分类 */
    private Long categoryId;

    /** 价格下限（最低价），不传=不限 */
    private BigDecimal minPrice;

    /** 价格上限（最高价），不传=不限 */
    private BigDecimal maxPrice;

    /** 标题关键词模糊搜索，不传=不搜 */
    private String keyword;

    /** 排序方式：time按最新(默认) / priceAsc价格从低到高 / priceDesc价格从高到低 */
    private String orderBy;
}
