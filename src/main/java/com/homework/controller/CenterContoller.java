package com.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/user")
public class CenterContoller extends BaseController{

    @GetMapping("/center")
    public String center(){
        log.info("-------------->进入个人中心");

        return "user/center";
    }
}
