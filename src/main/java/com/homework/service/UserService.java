package com.homework.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homework.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yan'gaoting
 * @since 2018-11-01
 */
public interface UserService extends IService<User> {
    /**
     * 给关联用户的分页结果添加用户信息
     * @param pageData
     * @param linkfield
     */
    void join(IPage<Map<String,Object>> pageData, String linkfield);
}
