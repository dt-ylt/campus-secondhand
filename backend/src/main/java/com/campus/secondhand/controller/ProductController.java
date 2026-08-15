package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.secondhand.common.result.Result;
import com.campus.secondhand.dto.ProductPublishDTO;
import com.campus.secondhand.dto.ProductQueryDTO;
import com.campus.secondhand.service.ProductService;
import com.campus.secondhand.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 商品接口
 *
 * RESTful 风格：
 *   POST   /product/publish   发布
 *   PUT    /product/{id}    编辑（PUT 表示"更新资源"）
 *   DELETE /product/{id}    删除（DELETE 表示"删除资源"）
 *   GET    /product/{id}    查详情（GET 表示"查资源"）
 *   GET    /product/list      分页列表（浏览用，不用登录）
 *   GET    /product/my        我发布的
 * @PathVariable：从 URL 路径里取值，比如 /product/5 里的 5 就是 id
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /** 发布商品  POST /product/publish */
    @PostMapping("/publish")
    public Result<Void> publish(@RequestAttribute("userId") Long userId,
                                @Valid @RequestBody ProductPublishDTO dto) {
        productService.publish(userId, dto);
        return Result.success();
    }

    /** 编辑商品  PUT /product/{id} */
    @PutMapping("/{id}")
    public Result<Void> update(@RequestAttribute("userId") Long userId,
                               @PathVariable Long id,
                               @Valid @RequestBody ProductPublishDTO dto) {
        productService.update(userId, id, dto);
        return Result.success();
    }

    /** 删除商品  DELETE /product/{id} */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestAttribute("userId") Long userId,
                               @PathVariable Long id) {
        productService.delete(userId, id);
        return Result.success();
    }

    /** 商品详情  GET /product/{id} */
    @GetMapping("/{id}")
    public Result<ProductVO> detail(@PathVariable Long id) {
        return Result.success(productService.getDetail(id));
    }

    /**
     * 商品分页列表  GET /product/list?pageNum=1&pageSize=10&categoryId=2&minPrice=10&maxPrice=100&keyword=键盘&orderBy=time
     * 注意：GET 请求参数在 URL 上，不写 @RequestBody，Spring 自动按名字绑定到 DTO 字段
     */
    @GetMapping("/list")
    public Result<IPage<ProductVO>> list(ProductQueryDTO dto) {
        return Result.success(productService.pageQuery(dto));
    }

    /** 我发布的商品  GET /product/my */
    @GetMapping("/my")
    public Result<List<ProductVO>> my(@RequestAttribute("userId") Long userId) {
        return Result.success(productService.myProducts(userId));
    }
}
