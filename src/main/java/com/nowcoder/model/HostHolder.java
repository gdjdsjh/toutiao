package com.nowcoder.model;

import org.springframework.stereotype.Component;

/**
 * Created by sjh on 2016/7/17.
 * Hostholder用来存储本次访问的人是谁
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser(){
        return users.get();
    }

    public void setUser(User user){
        users.set(user);
    }

    public void clear(){
        users.remove();
    }
}
