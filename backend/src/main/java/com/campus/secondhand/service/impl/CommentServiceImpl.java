package com.campus.secondhand.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.secondhand.common.exception.BusinessException;
import com.campus.secondhand.dto.CommentAddDTO;
import com.campus.secondhand.entity.Product;
import com.campus.secondhand.entity.ProductComment;
import com.campus.secondhand.entity.User;
import com.campus.secondhand.mapper.ProductCommentMapper;
import com.campus.secondhand.mapper.ProductMapper;
import com.campus.secondhand.mapper.UserMapper;
import com.campus.secondhand.service.CommentService;
import com.campus.secondhand.vo.CommentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品留言业务实现
 */
@Service
public class CommentServiceImpl extends ServiceImpl<ProductCommentMapper, ProductComment> implements CommentService {

    @Autowired
    private ProductCommentMapper commentMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 发表留言
     */
    @Override
    public void add(Long userId, CommentAddDTO dto) {
        // 商品要存在
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        ProductComment comment = new ProductComment();
        comment.setProductId(dto.getProductId());
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        comment.setParentId(0L);   // 顶级留言（回复功能预留）
        commentMapper.insert(comment);
    }

    /**
     * 某商品的留言列表（分页 + 批量拼留言人昵称头像，避免N+1）
     */
    @Override
    public IPage<CommentVO> listByProduct(Long productId, Integer pageNum, Integer pageSize) {
        // 1. 分页查留言（最新在前）
        Page<ProductComment> page = commentMapper.selectPage(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<ProductComment>()
                        .eq(ProductComment::getProductId, productId)
                        .orderByDesc(ProductComment::getCreateTime));

        // 2. 批量查留言人信息
        List<ProductComment> records = page.getRecords();
        List<CommentVO> voList = new ArrayList<>();
        if (!records.isEmpty()) {
            Set<Long> userIds = records.stream()
                    .map(ProductComment::getUserId)
                    .collect(Collectors.toSet());
            Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));

            // 3. 组装 VO
            for (ProductComment c : records) {
                CommentVO vo = new CommentVO();
                BeanUtils.copyProperties(c, vo);
                User u = userMap.get(c.getUserId());
                if (u != null) {
                    vo.setNickname(u.getNickname());
                    vo.setAvatar(u.getAvatar());
                }
                voList.add(vo);
            }
        }

        // 4. VO列表 + 分页信息
        Page<CommentVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }
}
