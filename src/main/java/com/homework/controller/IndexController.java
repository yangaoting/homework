package com.homework.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homework.entity.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Slf4j
@Controller
public class IndexController extends BaseController{

    @GetMapping("/")
    public String index(){

        Page<Post> page = new Page<Post>();
        page.setCurrent(1);
        page.setSize(10);

        IPage<Map<String, Object>> pageData = postService.pageMaps(page, null);
        //添加关联的用户信息
        userService.join(pageData,"user_id");

        req.setAttribute("pageData",pageData);
        log.info("------------->" + pageData.getRecords());
        log.info("--------------------------" + page.getPages());
        return "index";
    }
}

