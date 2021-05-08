package com.cimonhe;

import com.cimonhe.mapper.UserMapper;
import com.cimonhe.service.GoodsService;
import com.cimonhe.service.UserService;
import com.cimonhe.utils.ValidateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

@SpringBootTest
class SecondHandmarketApplicationTests {
    //DI注入数据源
    @Autowired
    DataSource dataSource;

    @Autowired
    GoodsService goodsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

//    @Test
//    public void contextLoads() throws SQLException {
//        //看一下默认数据源
//        System.out.println(dataSource.getClass());
//        //获得连接
//        Connection connection =   dataSource.getConnection();
//        System.out.println(connection);
//        //关闭连接
//        connection.close();
//    }

    @Test
    public void test1(){
        System.out.println(passwordEncoder.encode("123456"));
        System.out.println(passwordEncoder.matches("123456","$2a$10$6TDED9BBed4NKAbYTvS2QOB0Nd2Nl3F8arTBW72hT/6vxeU7SCW9O"));
    }

    @Test
    public void test2(){
////        System.out.println(goodsService.cntByTagId(1));
//        userService.deleteUserById(12);
        System.err.println((int)((Math.random() * 9 + 1) * 100000));
    }


    @Test
    public void test3(){
        System.err.println(ValidateUtil.checkFile("123"));
    }


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void contextLoads() {
//        //向redis中添加数据
//        redisTemplate.opsForValue().set("keys", "杨程显是傻逼");
//        System.out.println(redisTemplate.opsForValue().get("tokenVersion" + 1));
//        redisTemplate.delete("tokenVersion"+1);
//        //根据键值取出数据
//        System.out.println(redisTemplate.opsForValue().get("keys"));
        redisTemplate.delete("tokenVersion"+1);


    }
}
