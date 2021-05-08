package com.cimonhe.mapper;

import com.cimonhe.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserMapper {

    List<User> queryAllUser();

    User queryUserByNamePwd(Map<String,String> map);

    User queryUserByEmailPwd(Map<String,String> map);

    User queryUserByName(String username);

    User queryUserById(int id);

    int deleteUserById(int id);

    int incUnqualifiedNumById(int id);

    User queryUserByEmail(String email);

    int addUser(User user);

    int upGenderById(Map<String,Object> map);

    int upGradeById(Map<String,Object> map);

    int upSnoById(Map<String,Object> map);

    int upAcademyById(Map<String,Object> map);

    int upMajorById(Map<String,Object> map);

    int upIntroductionById(Map<String,Object> map);

    int upPasswordById(Map<String,Object> map);

    int upEmailById(Map<String,Object> map);

    int upUsernameById(Map<String,Object> map);

    List<User> queryUserMoreUnqualifiedNum(int unqualifiedNum);
    int deleteUserMoreUnqualifiedNum(int unqualifiedNum);
}
