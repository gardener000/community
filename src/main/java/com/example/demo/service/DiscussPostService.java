package com.example.demo.service;

import com.example.demo.dao.DiscussPostMapper;
import com.example.demo.entity.DiscussPost;
import com.example.demo.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(offset, limit);
    }
    public int findDiscussPostRows() {
        return discussPostMapper.selectDiscussPostRows();
    }

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        // 1. 转义 HTML 标签 (使用 org.springframework.web.util.HtmlUtils)
        post.setTitle(org.springframework.web.util.HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(org.springframework.web.util.HtmlUtils.htmlEscape(post.getContent()));

        // 2. 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }


    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    // 在 DiscussPostService 中添加以下方法
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }
}