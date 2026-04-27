package com.example.demo.controller;

import com.example.demo.entity.Comment;
import com.example.demo.entity.User;
import com.example.demo.service.CommentService;
import com.example.demo.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;


@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        User user = hostHolder.getUser();
        if (user == null) {
            return "redirect:/login"; // 没登录不能评论
        }

        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        // 设置评论目标（这里我们先简单处理，只做对帖子的评论）
        comment.setEntityType(1);
        comment.setEntityId(discussPostId);

        commentService.addComment(comment);

        return "redirect:/discuss/detail/" + discussPostId; // 重定向回详情页
    }
}