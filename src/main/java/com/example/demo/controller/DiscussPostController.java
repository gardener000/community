package com.example.demo.controller;

import com.example.demo.service.DiscussPostService;
import com.example.demo.service.CommentService;
import com.example.demo.entity.Comment;
import com.example.demo.entity.DiscussPost;
import com.example.demo.entity.Page;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.CommunityUtil;
import com.example.demo.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody // 关键：表示返回的是字符串(JSON)而不是网页
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "您还没有登录哦!");
        }

        DiscussPost post = new DiscussPost();
        // 使用 String.valueOf() 将 int 转换为 String
        post.setUserId(String.valueOf(user.getId()));
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new java.util.Date());
        discussPostService.addDiscussPost(post);

        // 报错情况将来统一处理
        return CommunityUtil.getJSONString(0, "发布成功!");
    }
    @Autowired
    private UserService userService; // 用于查询作者信息

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 1. 查询帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);

        // 2. 查询作者
        User user = userService.findUserById(Integer.parseInt(post.getUserId()));
        // 【保护逻辑】如果找不到作者，给一个虚拟的“神秘人”对象，防止页面崩溃
        if (user == null) {
            user = new User();
            user.setUsername("神秘用户");
            user.setHeaderUrl("http://images.nowcoder.com/head/0t.png");
        }
        model.addAttribute("user", user);
        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount()); // 记得给帖子表加个 comment_count 字段

        // 1. 查询该帖子的评论列表 (EntityType = 1)
        List<Comment> commentList = commentService.findCommentsByEntity(1, post.getId(), page.getOffset(), page.getLimit());

        // 2. 构造一个复杂的 List<Map>，用来装“评论+作者+回复列表”
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                // 查询评论人
                User user1 = userService.findUserById(comment.getUserId());
                // 【核心改进】：如果找不到用户，给一个虚拟的“匿名用户”对象，防止页面崩溃
                if (user1 == null) {
                    user1 = new User();
                    user1.setUsername("已注销用户");
                    user1.setHeaderUrl("http://images.nowcoder.com/head/0t.png"); // 给个默认头像
                }
                commentVo.put("user", user1);

                // --- 开始补全：查询回复列表 ---
                List<Comment> replyList = commentService.findCommentsByEntity(
                        2, comment.getId(), 0, Integer.MAX_VALUE); // EntityType=2 代表回复

                // 回复的VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复本身
                        replyVo.put("reply", reply);
                        // 发回复的人
                        User user2 = userService.findUserById(comment.getUserId());
                        // 【核心改进】：如果找不到用户，给一个虚拟的“匿名用户”对象，防止页面崩溃
                        if (user2 == null) {
                            user2 = new User();
                            user2.setUsername("已注销用户");
                            user2.setHeaderUrl("http://images.nowcoder.com/head/0t.png"); // 给个默认头像
                        }
                        replyVo.put("user", user2);

                        // 回复的目标（比如：张三 回复 李四，这里的李四就是 target）
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replies", replyVoList); // 将补全后的回复列表塞进评论VO

                // 该评论的回复数量
                int replyCount = commentService.findCommentCount(2, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);
        return "demo/site/discuss-detail";
    }

}