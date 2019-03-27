package com.homework.controller;

import com.homework.service.*;
import com.homework.shiro.AccountProfile;
import com.homework.utils.RedisUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    @Autowired
    HttpServletRequest req;

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    UserMessageService userMessageService;

    @Autowired
    UserCollectionService userCollectionService;

    @Autowired
    CommentService commentService;

    @Autowired
    RedisUtil redisUtil;

    protected AccountProfile getProfile() {
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }

    protected Long getProfileId() {
        return getProfile().getId();
    }
}
