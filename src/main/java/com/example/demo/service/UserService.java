package com.example.demo.service;

import com.example.demo.dao.LoginTicketMapper;
import com.example.demo.dao.UserMapper;
import com.example.demo.entity.LoginTicket;
import com.example.demo.entity.User;
import com.example.demo.util.CommunityUtil;
import com.example.demo.util.MailClient;
import com.example.demo.util.CommunityConstant;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.demo.util.CommunityConstant.*;


/**
 * Entity (User): 相当于一封信。它只负责保存内容，自己不会飞。
 * Mapper (UserMapper): 相当于邮递员。只有他能把信（User 对象）送进邮局（数据库）。
 * Service (UserService): 相当于写信的过程。他负责决定写什么、给谁写、什么时候发。
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    // 注入你在 properties 里配置的域名，以后激活链接要用到
    @Value("${community.path.domain}")
    private String domain;

    /**
     * 注册逻辑
     * 返回 Map 用于存储错误消息，如果 Map 为空则表示注册成功
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 1. 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        // 2. 验证账号是否已存在
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }

        // 3. 验证邮箱是否已存在
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }

        // 4. 注册用户（核心步骤）
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5)); // 生成5位随机盐
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt())); // 密码加盐加密
        user.setType(0); // 默认普通用户
        user.setStatus(0); // 默认未激活
        user.setActivationCode(CommunityUtil.generateUUID()); // 生成随机激活码
        // 设置随机头像 (使用牛客网提供的测试头像地址)
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user); // 存入数据库

        // 5. 发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // 激活链接示例: http://localhost:8080/activation/用户ID/激活码
        String url = domain + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("demo/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活您的牛客社区账号", content);

        return map;
    }
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId); // 注意：你可能需要在 UserMapper 里补一个 selectById 方法
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    /**
     * 登录逻辑
     * @return 返回包含错误信息或凭证(ticket)的 Map
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 1. 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 2. 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }

        // 3. 验证密码 (注意：要用 库里的盐 重新加密后再对比)
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 4. 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    // 退出功能
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }
    public int updateHeader(int id, String headerUrl) {
        return userMapper.updateHeader(id, headerUrl);
    }
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }
}