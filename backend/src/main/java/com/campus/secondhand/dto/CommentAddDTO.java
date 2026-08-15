package com.campus.secondhand.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 发表留言的请求参数
 */
@Data
public class CommentAddDTO {

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotBlank(message = "留言内容不能为空")
    @Size(max = 500, message = "留言最多500字")
    private String content;
}
