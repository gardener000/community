package com.example.demo.entity;

import java.util.Date;

public class Message {
    private int id;
    private int fromId;
    // 实体类型：1代表帖子，2代表评论（即回复）
    private int toId;
    // 实体ID：对应帖子ID或评论ID
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getFromId() {
        return fromId;
    }

    public int getId() {
        return id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public int getToId() {
        return toId;
    }

    public String getContent() {
        return content;
    }

    public int getStatus() {
        return status;
    }

    public Date getCreateTime() {
        return createTime;
    }
}
