package com.campus.secondhand.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.secondhand.common.result.Result;
import com.campus.secondhand.dto.CommentAddDTO;
import com.campus.secondhand.service.CommentService;
import com.campus.secondhand.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 商品留言接口
 *   发留言要登录；看留言不用登录（商品页公开浏览）
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /** 发表留言  POST /comment/add */
    @PostMapping("/add")
    public Result<Void> add(@RequestAttribute("userId") Long userId,
                            @Valid @RequestBody CommentAddDTO dto) {
        commentService.add(userId, dto);
        return Result.success();
    }

    /** 留言列表  GET /comment/list/{productId}?pageNum=1&pageSize=20 */
    @GetMapping("/list/{productId}")
    public Result<IPage<CommentVO>> list(@PathVariable Long productId,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(commentService.listByProduct(productId, pageNum, pageSize));
    }
}
