package com.homework.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.homework.shiro.OAuth2Realm;
import org.apache.shiro.mgt.SecurityManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Configuration
public class ShiroConfig {
    @Bean("securityManager")
    public SecurityManager securityManager(OAuth2Realm oAuth2Realm){
        DefaultWebSecurityManager securityManager  = new DefaultWebSecurityManager();

        securityManager.setRealm(oAuth2Realm);
        log.info("---------------->securityManager注入完成");

        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("securityManager") SecurityManager securityManager){
        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();

        filterFactoryBean.setSecurityManager(securityManager);
        //配置登陆的url和登陆成功的url
        filterFactoryBean.setLoginUrl("/login");
        filterFactoryBean.setSuccessUrl("/user/center");
        //配置未授权跳转页面
        filterFactoryBean.setUnauthorizedUrl("/error/403");

        Map<String,String> hashMap = new LinkedHashMap<>();
        hashMap.put("/login","anon");
        hashMap.put("/user*","user");
        hashMap.put("/user/**","user");
        hashMap.put("/post/**","user");

        filterFactoryBean.setFilterChainDefinitionMap(hashMap);

        return filterFactoryBean;
    }

    @Bean
    public ShiroDialect shiroDialect(){
        return new ShiroDialect();
    }
}
