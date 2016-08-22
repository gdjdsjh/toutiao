package com.nowcoder.controller;

import com.nowcoder.model.News;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjh on 2016/7/9.
 */
@Controller
public class HomeController {
    @Autowired
    UserService userService;
    @Autowired
    NewsService newsService;


    private List<ViewObject> getNews(int userId, int offset, int limit){
        List<News> newsList = newsService.getLatestNews(userId, offset, limit);

        List<ViewObject> vos = new ArrayList<>();

        for(News news : newsList){
            ViewObject vo = new ViewObject();
            vo.set("news",news);
            vo.set("user",userService.getUser(news.getUserId()));
            vos.add(vo);
        }

        return vos;
    }


    @RequestMapping(path = {"/","/index"})
    public String index(Model model,
                        @RequestParam(value = "pop ",defaultValue = "0") int pop) {
        model.addAttribute("vos", getNews(0, 0, 10));
        model.addAttribute("pop",pop);
        return "home";
    }


    @RequestMapping(path = {"/user/{userId}"})
    public String userIndex(Model model, @PathVariable("userId") int userId) {

        model.addAttribute("vos", getNews(userId, 0, 10));
        return "home";
    }
}
