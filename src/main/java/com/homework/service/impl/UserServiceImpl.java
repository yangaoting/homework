package com.homework.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homework.entity.User;
import com.homework.mapper.UserMapper;
import com.homework.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yan'gaoting
 * @since 2018-11-01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public void join(IPage<Map<String, Object>> pageData, String linkfield) {
        List<Map<String, Object>> records = pageData.getRecords();

        records.forEach(map ->{
            String userId = map.get(linkfield).toString();
            User user = this.getById(userId);

            Map<String,Object> author = new HashMap<>();
            author.put("username",user.getUsername());
            author.put("email",user.getEmail());
            author.put("avatar",user.getAvatar());
            author.put("id",user.getId());

            map.put("author",author);
        });
    }
}
