package com.example.demo.dao;

import com.example.demo.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper // 这个注解非常重要，告诉 Spring 这是 MyBatis 的接口
public interface DiscussPostMapper {
    // 查询帖子列表（为了以后做分页，我们加上 offset 和 limit）
    List<DiscussPost> selectDiscussPosts(int offset, int limit);
    // 查询帖子总数
    int selectDiscussPostRows();
}