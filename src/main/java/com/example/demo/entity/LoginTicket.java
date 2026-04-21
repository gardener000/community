package com.example.demo.entity;

import java.util.Date;

public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getTicket() {
        return ticket;
    }

    public int getStatus() {
        return status;
    }

    public Date getExpired() {
        return expired;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }
}

