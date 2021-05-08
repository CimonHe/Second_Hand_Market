package com.cimonhe.service;

import com.cimonhe.pojo.User;

import java.util.List;

public interface UserService {

    List<User> queryAllUser();

    User queryUserByNamePwd(String username,String password);

    User queryUserByEmailPwd(String email, String password);

    User queryUserById(int id);

    User queryUserByName(String username);

    int deleteUserById(int id);

    int incUnqualifiedNumById(int id);

    User queryUserByEmail(String email);

    int addUser(User user);

    int upGenderById(int id,boolean gender);

    int upGradeById(int id,String grade);

    int upSnoById(int id,String sno);

    int upAcademyById(int id,String academy);

    int upMajorById(int id,String major);

    int upIntroductionById(int id, String introduction);

    int upPasswordById(int id,String password);

    int upEmailById(int id,String email);

    int upUsernameById(int id,String username);

    List<User> queryUserMoreUnqualifiedNum(int unqualifiedNum);

    int deleteUserMoreUnqualifiedNum(int unqualifiedNum);

}
