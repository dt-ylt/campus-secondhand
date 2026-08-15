package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.secondhand.common.result.Result;
import com.campus.secondhand.entity.Category;
import com.campus.secondhand.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品分类接口
 *
 * 简单查询直接用 Mapper，不单独建 Service 层——分层是为了组织复杂逻辑，
 * 一句查全表没必要多包一层（这是常见做法，别死板）。
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    /** 分类列表  GET /category/list（首页筛选用、发布页下拉用） */
    @GetMapping("/list")
    public Result<List<Category>> list() {
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
        return Result.success(categories);
    }
}
