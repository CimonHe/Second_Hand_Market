package com.cimonhe.service;

import com.cimonhe.mapper.UserMapper;
import com.cimonhe.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserMapper userMapper;

    @Override
    public List<User> queryAllUser() {
        return userMapper.queryAllUser();
    }

    @Override
    public User queryUserByNamePwd(String username,String password) {
        Map<String,String> map = new HashMap<>();
        map.put("username",username);
        map.put("password",password);
        System.out.println(userMapper.queryUserByNamePwd(map));
        return userMapper.queryUserByNamePwd(map);
    }

    @Override
    public User queryUserByEmailPwd(String email, String password) {
        Map<String,String> map = new HashMap<>();
        map.put("email",email);
        map.put("password",password);
        System.out.println(userMapper.queryUserByEmailPwd(map));
        return userMapper.queryUserByEmailPwd(map);
    }

    @Override
    public User queryUserById(int id) {
        return userMapper.queryUserById(id);
    }

    @Override
    public User queryUserByName(String username) {
        return userMapper.queryUserByName(username);
    }

    @Override
    public int deleteUserById(int id) {
        return userMapper.deleteUserById(id);
    }

    @Override
    public int incUnqualifiedNumById(int id) {
        return userMapper.incUnqualifiedNumById(id);
    }

    @Override
    public User queryUserByEmail(String email) {
        return userMapper.queryUserByEmail(email);
    }

    @Override
    public int addUser(User user) {
        return userMapper.addUser(user);
    }

    @Override
    public int upGenderById(int id, boolean gender) {
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("gender",gender);
        return userMapper.upGenderById(map);
    }

    @Override
    public int upGradeById(int id, String grade) {
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("grade",grade);
        return userMapper.upGradeById(map);
    }

    @Override
    public int upSnoById(int id, String sno) {
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("sno",sno);
        return userMapper.upSnoById(map);
    }

    @Override
    public int upAcademyById(int id, String academy) {
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("academy",academy);
        return userMapper.upAcademyById(map);
    }

    @Override
    public int upMajorById(int id, String major) {
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("major",major);
        return userMapper.upMajorById(map);
    }

    @Override
    public int upIntroductionById(int id, String introduction) {
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("introduction",introduction);
        return userMapper.upIntroductionById(map);
    }

    @Override
    public int upPasswordById(int id, String password) {
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("password",password);
        return userMapper.upPasswordById(map);
    }

    @Override
    public int upEmailById(int id, String email) {
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("email",email);
        return userMapper.upEmailById(map);
    }

    @Override
    public int upUsernameById(int id, String username) {
        Map<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("username",username);
        return userMapper.upUsernameById(map);
    }

    @Override
    public List<User> queryUserMoreUnqualifiedNum(int unqualifiedNum) {
        return userMapper.queryUserMoreUnqualifiedNum(unqualifiedNum);
    }

    @Override
    public int deleteUserMoreUnqualifiedNum(int unqualifiedNum) {
        return userMapper.deleteUserMoreUnqualifiedNum(unqualifiedNum);
    }

}
