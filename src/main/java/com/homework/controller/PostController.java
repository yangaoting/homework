package com.homework.controller;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.homework.entity.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Slf4j
@Controller
public class PostController extends BaseController {

    @GetMapping("/post/{id}")
    public String index(@PathVariable long id){
        Map<String, Object> post = postService.getMap(new QueryWrapper<Post>().eq("id", id));
        userService.join(post,"user_id");

        Assert.notNull(post,"该文章已被删除");

        req.setAttribute("post",post);

        return "post";
    }

}
