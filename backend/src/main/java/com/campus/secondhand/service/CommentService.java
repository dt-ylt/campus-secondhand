package com.campus.secondhand.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.secondhand.dto.CommentAddDTO;
import com.campus.secondhand.entity.ProductComment;
import com.campus.secondhand.vo.CommentVO;

/**
 * 商品留言业务层接口
 */
public interface CommentService extends IService<ProductComment> {

    /** 发表留言 */
    void add(Long userId, CommentAddDTO dto);

    /** 某商品的留言列表（分页，带留言人昵称头像） */
    IPage<CommentVO> listByProduct(Long productId, Integer pageNum, Integer pageSize);
}
