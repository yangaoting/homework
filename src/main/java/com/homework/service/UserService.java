package com.homework.service;

import com.baomidou.mybatisplus.extension.api.R;
import com.homework.entity.User;
import com.homework.shiro.AccountProfile;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yan'gaoting
 * @since 2018-11-01
 */
public interface UserService extends BaseService<User> {

    /**
     * 注册
     * @param user
     * @return
     */
    R register(User user);

    /**
     *  用于用户登陆
     *  AccountProfile是有用户基本信息的类，包括私信，通知数量，头像等
     * @param email
     * @param password
     * @return
     */
    AccountProfile login(String email, String password);
}
