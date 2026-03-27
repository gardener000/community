package com.example.demo;

import com.example.demo.dao.DiscussPostMapper;
import com.example.demo.entity.DiscussPost;
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
}