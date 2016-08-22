package com.nowcoder.controller;

import com.nowcoder.model.User;
import com.nowcoder.service.ToutiaoService;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by sjh on 2016/7/3.
 */
//@Controller
public class IndexController {

    @Autowired
    private ToutiaoService toutiaoService;

    @RequestMapping(path = {"/","/index"})
    @ResponseBody
    public String index(HttpSession session){
        return "Hello nowcoder, " + session.getAttribute("msg")
                + "<br> Say:" + toutiaoService.say() ;
    }

    @RequestMapping(value={"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("groupId") String groupId,
                          @PathVariable("userId") int userId,
                          @RequestParam(value = "type", defaultValue = "1") int type,
                          @RequestParam(value = "key", defaultValue = "nowcoder") String key){
        return String.format("GID{%s},UID{%d},TYPE{%s},KEY{%s}",groupId,userId,type,key);
    }

    @RequestMapping("/vm")
    public String news(Model model){
        model.addAttribute("value1","vv1");
        List<String> colors = Arrays.asList(new String[]{"RED","GREEN","BLUE"});

        Map<String, String> map = new HashMap<>();
        for(int i = 0; i < 4; ++i ){
            map.put(String.valueOf(i),String.valueOf(i*i));
        }

        model.addAttribute("colors", colors);
        model.addAttribute("map",map);

        model.addAttribute("user",new User("Jim"));

        return "news";
    }

    @RequestMapping("/request")
    @ResponseBody
    public String request(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session){
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            sb.append(name + ":" + request.getHeader(name) + "<br>");
        }

        for(Cookie cookie : request.getCookies()){
            sb.append("Cookie:");
            sb.append(cookie.getName());
            sb.append(":");
            sb.append(cookie.getValue());
            sb.append("<br>");
        }

        return  sb.toString();
    }

    @RequestMapping("/response")
    @ResponseBody
    public String response(@CookieValue(value="nowcoderid", defaultValue = "a") String nowcoderId,
                           @RequestParam(value = "key",defaultValue = "key") String key,
                           @RequestParam(value = "value", defaultValue = "value") String value,
                           HttpServletResponse response){
        response.addCookie(new Cookie(key,value));
        response.addHeader(key,value);
        return "NowCoderId from Cookie:" + nowcoderId;
    }


    @RequestMapping("/redirect/{code}")
    public String redirect(@PathVariable("code") int code){
/*        RedirectView red = new RedirectView("/",true);
        if (code == 301){
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return red;*/

        return "redirect:/";
    }

    @RequestMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam(value = "key", required = false) String key){
        if("admin".equals(key)){
            return "hello admin";
        }
        throw new IllegalArgumentException("Key 错误");
    }

    //统一错误处理
    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e){
        return "ERROR:" + e.getMessage();
    }
}
