package com.example.demo.controller;

import com.example.demo.entity.DiscussPost;
import com.example.demo.entity.Page;
import com.example.demo.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class HomeController {
    // *1. 定义一个 logger 对象
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private DiscussPostService discussPostService;

    @RequestMapping("/index")
    public String getIndexPage(Model model, Page page) {
        // *2. 打印日志（比 print 更好，因为它带时间戳和线程信息）
        logger.debug("正在访问首页，当前页码: {}", page.getCurrent());
        // 1. 设置分页信息
        page.setRows(discussPostService.findDiscussPostRows());
        page.setPath("/index");

        // 2. 查询该页的帖子数据 (利用 page.getOffset() 获取起始位置)
        List<DiscussPost> list = discussPostService.findDiscussPosts(page.getOffset(), page.getLimit());

        // 3. 将数据和分页对象都传给网页
        model.addAttribute("discussPosts", list);
        // 注意：Spring MVC 会自动把 Page 对象也存入 model，名字默认叫 "page"

        return "demo/index";
    }
}