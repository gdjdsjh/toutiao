package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.krb5.internal.Ticket;

import java.util.*;

/**
 * Created by sjh on 2016/7/9.
 */
@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;

    public Map<String, Object> register(String username, String password){
        Map<String, Object> map = new HashMap<>();

        if(StringUtils.isBlank(username)){
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        //可以增加对密码强度的检测啊什么的

        User user = userDAO.selectByName(username);
        if(user != null){
           map.put("msgname","用户名已经被注册");
            return map;
        }

        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(100)));
        user.setPassword(ToutiaoUtil.MD5(password+user.getSalt()));
        userDAO.addUser(user);

        //登录
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);

        return map;

    }

    public Map<String, Object> login(String username, String password){
        Map<String, Object> map = new HashMap<>();

        if(StringUtils.isBlank(username)){
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("msgpwd", "密码不能为空");
            return map;
        }


        User user = userDAO.selectByName(username);
        if(user == null){
            map.put("msgname","用户名不存在");
            return map;
        }
        if(!ToutiaoUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msgpwd","密码不正确");
            return map;
        }

        //ticket，
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;

    }

    private String addLoginTicket(int userId){
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }


    public User getUser(int id){
        return userDAO.selectById(id);
    }

    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket,1);
    }
}

