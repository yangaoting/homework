package com.homework.shiro;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.api.R;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.UserFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class AuthFilter extends UserFilter {
    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletRequest req = (HttpServletRequest)request;

        String header = req.getHeader("X-Requested-With");
        if(header != null && "XMLHttpRequest".equals(header)){
            Subject subject = SecurityUtils.getSubject();
            if(!subject.isAuthenticated()){
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().print(JSONUtil.toJsonStr(R.failed("请先登录")));
            }
        }else{
            super.redirectToLogin(request, response);
        }
    }
}
