package com.wzp.demo.listener2;

public class ListenerEvent {

    private Integer userId;

    private String name;

    public ListenerEvent(Integer userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ListenerEvent{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                '}';
    }
}
