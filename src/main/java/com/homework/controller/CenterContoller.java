package com.homework.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homework.entity.Post;
import com.homework.entity.User;
import com.homework.entity.UserCollection;
import com.homework.entity.UserMessage;
import com.homework.shiro.AccountProfile;
import com.homework.utils.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/user")
public class CenterContoller extends BaseController{

    @GetMapping("/center")
    public String center(@RequestParam(defaultValue = "1") Integer current,@RequestParam(defaultValue = "10") Integer size){

        Page<Post> page = new Page<>();
        page.setCurrent(current);
        page.setSize(size);

        log.info("-------------->进入个人中心");

        IPage<Map<String, Object>> pageData = postService.pageMaps(page, new QueryWrapper<Post>().eq("user_id", getProfileId()).orderByDesc("created"));
        req.setAttribute("pageData",pageData);

        return "user/center";
    }

    @GetMapping("/collection")
    public String collection(@RequestParam(defaultValue = "1") Integer current,@RequestParam(defaultValue = "10") Integer size){
        Page<UserCollection> page = new Page<>();
        page.setCurrent(current);
        page.setSize(size);

        IPage<Map<String, Object>> pageData = userCollectionService.pageMaps(page,
                new QueryWrapper<UserCollection>().eq("user_id", getProfileId()).orderByDesc("created"));

        postService.join(pageData,"post_id");

        req.setAttribute("pageData",pageData);

        return "user/collection";
    }

    @ResponseBody
    @PostMapping("/message/nums")
    public R getMESSNums(){
        Map<String,Object> result = new HashMap<>();

        result.put("status",0);
        result.put("count",2);

        return R.ok(result);
    }

    @GetMapping("/setting")
    public String setting(){
        User user = userService.getById(getProfileId());
        user.setPassword(null);

        req.setAttribute("user",user);

        return "user/setting";
    }

    @ResponseBody
    @PostMapping("/setting")
    public R postSetting(User user){
        User tempUser = userService.getById(getProfileId());

        tempUser.setUsername(user.getUsername());
        tempUser.setGender(user.getGender());
        tempUser.setMobile(user.getMobile());
        tempUser.setSign(user.getSign());

        boolean isSucc = userService.updateById(tempUser);
        if(isSucc){
            AccountProfile profile = getProfile();
            profile.setUsername(tempUser.getUsername());
            profile.setGender(tempUser.getGender());
        }

        return isSucc ? R.ok(user) : R.failed("更新失败");
    }

    @ResponseBody
    @PostMapping("/upload")
    public R upload(@RequestParam(value = "file") MultipartFile file,
                    @RequestParam(value = "type",defaultValue = "avatar") String type){
        if(file.isEmpty()){
            return R.failed("上传失败");
        }
        //获取文件路径
        String filename = file.getOriginalFilename();
        log.info("上传文件名" + filename);
        //文件后缀
        String suffixName = filename.substring(filename.lastIndexOf("."));
        log.info("文件后缀名" + suffixName);
        //文件上传路径
        String filePath = Constant.uploadDir;

        if("avatar".equalsIgnoreCase(type)){
            filename = "/avatar/avatar_" + getProfileId() + suffixName;
        }else if("post".equalsIgnoreCase(type)){
            filename = "/post/post_" + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN) + suffixName;
        }
        File dest = new File(filePath + filename);
        //目录是否存在
        if(!dest.getParentFile().exists()){
            dest.getParentFile().mkdirs();
        }

        try {
            file.transferTo(dest);
            log.info("上传后的文件路径" + filePath + filename);

            String path = dest.getAbsolutePath();
            String url = Constant.uploadUrl + filename;

            log.info("url--->" + url);

            User current = userService.getById(getProfileId());
            current.setAvatar(url);
            userService.updateById(current);

            AccountProfile profile = getProfile();
            profile.setAvatar(url);

            return R.ok(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.ok(null);
    }

    @ResponseBody
    @PostMapping("/resetPwd")
    public R resetPwd(String nowpass,String pass){
        User user = userService.getById(getProfileId());
        String nowpassMd5 = SecureUtil.md5(nowpass);
        if(!nowpassMd5.equals(user.getPassword())){
            return R.failed("密码不正确");
        }
        user.setPassword(SecureUtil.md5(pass.trim()));
        userService.updateById(user);

        return R.ok(null);
    }

    @GetMapping("/message")
    public String message(@RequestParam(defaultValue = "1") Integer current,
                          @RequestParam(defaultValue = "10") Integer size){
        Page<UserMessage> page = new Page<>();
        page.setCurrent(current);
        page.setSize(size);

        IPage<Map<String, Object>> pageData = userMessageService.pageMaps(page,
                new QueryWrapper<UserMessage>().eq("to_user_id", getProfileId()).orderByDesc("created"));

        userService.join(pageData,"from_user_id");

        postService.join(pageData,"post_id");

        commentService.join(pageData,"comment_id");

        req.setAttribute("pageData",pageData);

        return "user/message";
    }

    @ResponseBody
    @PostMapping("/message/remove")
    public R removeMsg(Long id,boolean all){
        boolean res = userMessageService.remove(new QueryWrapper<UserMessage>().eq("to_user_id", getProfileId()).eq(!all, "id", id));

        return res ? R.ok(null) : R.failed("删除失败");
    }

}
