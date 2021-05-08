package com.cimonhe;

import com.cimonhe.mapper.UserMapper;
import com.cimonhe.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class UserTest {
    @Autowired
    UserMapper userMapper;
    @Test
    public void queryAllUser(){
        for (User user : userMapper.queryAllUser()) {
            System.out.println(user);
        }
    }

    @Test
    public void queryUserByNamePwd(){
        Map<String,String> map = new HashMap<>();
        map.put("username","黑泽明");
        map.put("password","123456");
        System.out.println(userMapper.queryUserByNamePwd(map));
    }

    @Test
    public void queryUserById(){
        System.out.println(userMapper.queryUserById(2));
    }

    @Test
    public void queryUserByName(){
        System.out.println(userMapper.queryUserByName("黑泽明"));
    }

    @Value("${auth.privateKey}")
    private String privateKey ;
    @Value("${auth.publicKey}")
    private String publicKey ;
    @Test
    public void test(){
        System.out.println(privateKey);
    }
}
