package com.homework.controller;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.code.kaptcha.Producer;
import com.homework.entity.Post;
import com.homework.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

@Slf4j
@Controller
public class IndexController extends BaseController{

    private static final String KAPTCHA_SESSION_KEY = "KAPTCHA_SESSION_KEY";
    //验证码的生成器
    @Autowired
    private Producer producer;

    @GetMapping("/")
    public String index(@RequestParam(defaultValue = "1") Integer current,
                        @RequestParam(defaultValue = "10") Integer size){

        Page<Post> page = new Page<Post>();
        page.setCurrent(current);
        page.setSize(size);

        IPage<Map<String, Object>> pageData = postService.pageMaps(page, new QueryWrapper<Post>()
                .orderByDesc("created"));

        //添加关联的用户信息
        userService.join(pageData,"user_id");
        categoryService.join(pageData,"category_id");

        req.setAttribute("pageData",pageData);
        log.info("------------->" + pageData.getRecords());
        log.info("--------------------------" + page.getPages());

        List<Map<String, Object>> levelPosts = postService.listMaps(new QueryWrapper<Post>().orderByDesc("level").last(" limit 5"));
        userService.join(levelPosts,"user_id");
        categoryService.join(levelPosts,"category_id");

        req.setAttribute("levalPosts",levelPosts);

        return "index";
    }

    @GetMapping("/capthca.jpg")
    public void captcha(HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control","no-store,no-cache");
        response.setContentType("image/jpeg");

        //生成文字验证码
        String text = producer.createText();
        //生成图片验证码
        BufferedImage image = producer.createImage(text);

        SecurityUtils.getSubject().getSession().setAttribute(KAPTCHA_SESSION_KEY,text);

        ServletOutputStream outputStream = response.getOutputStream();

        ImageIO.write(image,"jpg",outputStream);
    }

    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }

    @ResponseBody
    @PostMapping("/register")
    public R doRegister(User user, String captcha){
        String kaptcha = (String) SecurityUtils.getSubject().getSession().getAttribute(KAPTCHA_SESSION_KEY);

        if(!kaptcha.equalsIgnoreCase(captcha)){
            System.out.println(kaptcha + "-----" + captcha);
            return R.failed("验证码不正确");
        }

        R r = userService.register(user);

        return r;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @ResponseBody
    @PostMapping("/login")
    public R doLogin(String email,String password){
        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(password)){
            return R.failed("用户名或密码不能为空");
        }

        AuthenticationToken token = new UsernamePasswordToken(email, SecureUtil.md5(password));
        log.info(SecureUtil.md5(password));

        try {
            //尝试登陆，将会调用realm的认证方法
            SecurityUtils.getSubject().login(token);
        } catch (AuthenticationException e) {
            if(e instanceof UnknownAccountException){
                return R.failed("用户不存在");
            }else if(e instanceof LockedAccountException){
                return R.failed("用户被禁用");
            }else if(e instanceof IncorrectCredentialsException){
                return R.failed("密码错误");
            }else {
                return R.failed("用户认证失败");
            }
        }

        return R.ok("登陆成功");
    }

    @GetMapping("/user/logout")
    public String logout(){
        SecurityUtils.getSubject().logout();
        return "redirect:/";
    }


    /**
     * TODO 要使用redis有序列表实现
     * 思路：
     * 项目启动初始化最近7天发表文章的评论数量，
     * 发表评论给对应点的文章添加comment-count。同时设置有效期。
     *
     * 命令使用：
     * 1、ZINCRBY rank:20181020 5 1
     * 2、ZRANGE rank:20181020 0 -1 withscores
     * 3、ZREVRANGE rank:20181220 0 -1 withsroces
     * 4、ZINCRBY rank:20181019 99 1
     * 统计
     * 5、ZUNIONSTORE rank:last_week 7 rank:20181019 rank:20181020 rank:20181021 weights 1 1 1
     * 6、ZREVRANGE rank:last_week 0 -1 withscores
     * 设定有效期
     * 7、ttl rank:last_week
     * 8、expire rank:last_week 60*60*24*7
     * @return
     */
    @ResponseBody
    @GetMapping("/post/hots")
    public R hotPost(){
        Set<ZSetOperations.TypedTuple> lastWeekRank = redisUtil.getZsetRank("last_week_rank", 0, 6);

        ArrayList<Map<String,Object>> hotPosts = new ArrayList<>();
        for (ZSetOperations.TypedTuple typedTuple:lastWeekRank){
            Map<String ,Object> map = new HashMap<>();
            map.put("comment_count",typedTuple.getScore());
            map.put("id",redisUtil.hget("rank_post_" + typedTuple.getValue(),"post:id"));
            map.put("title",redisUtil.hget("rank_post_" + typedTuple.getValue(),"post:title"));

            hotPosts.add(map);
        }

        return R.ok(hotPosts);
    }
}

