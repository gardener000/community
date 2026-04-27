package com.example.demo.dao;

import com.example.demo.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    // 分页查询评论
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);
    // 查询评论总数
    int selectCountByEntity(int entityType, int entityId);
    int insertComment(Comment comment);
}