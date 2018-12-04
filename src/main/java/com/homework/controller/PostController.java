package com.homework.controller;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homework.entity.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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

        return "post/index";
    }

    @GetMapping("/category/{id}")
    public String category(@PathVariable Long id,
                           @RequestParam(defaultValue = "1") Integer current,
                           @RequestParam(defaultValue = "10") Integer size){

        Page<Post> page = new Page<>();
        page.setCurrent(current);
        page.setSize(size);

        IPage<Map<String, Object>> pageData = postService.pageMaps(page,
                new QueryWrapper<Post>().eq("category_id", id).orderByDesc("created"));

        userService.join(pageData,"user_id");
        categoryService.join(pageData,"category_id");

        req.setAttribute("pageData",pageData);
        req.setAttribute("currentCategoryId",id);

        return "post/category";
    }
}
