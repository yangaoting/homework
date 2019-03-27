package com.homework.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.homework.entity.Post;
import com.homework.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yan'gaoting
 * @since 2018-11-01
 */
@Controller
public class UserController extends BaseController{
    @RequestMapping("/u/{id}")
    public String home(@PathVariable Long id){
        User user = userService.getById(id);
        user.setPassword(null);

        Date date30before = DateUtil.offsetDay(new Date(), -30).toJdkDate();
        List<Post> posts = postService.list(new QueryWrapper<Post>()
                .eq("user_id", id)
                .ge("created", date30before)
                .orderByDesc("created"));

        req.setAttribute("user",user);
        req.setAttribute("posts",posts);

        return "user/home";
    }
}

