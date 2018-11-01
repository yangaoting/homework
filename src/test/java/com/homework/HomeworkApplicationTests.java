package com.homework;

import com.homework.entity.User;
import com.homework.mapper.UserMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HomeworkApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println("------select all method test");
        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(1L,userList.size());
        userList.forEach(System.out::println);
    }

}
