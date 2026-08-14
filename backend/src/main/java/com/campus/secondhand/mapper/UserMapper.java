package com.campus.secondhand.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.secondhand.entity.User;

/**
 * 用户 Mapper（数据访问层）
 *
 * 继承 BaseMapper<User> 后，MyBatis-Plus 自动给你一堆现成的数据库操作方法，不用写 SQL：
 *   insert(user)              插入一条用户
 *   selectById(id)            按ID查用户
 *   selectList(queryWrapper)  按条件查多个用户
 *   updateById(user)          按ID更新
 *   deleteById(id)            按ID删除
 *   等等...
 *
 * 不用加 @Mapper 注解，因为启动类上的 @MapperScan("...mapper") 会扫描这个包下所有接口。
 */
public interface UserMapper extends BaseMapper<User> {
}
