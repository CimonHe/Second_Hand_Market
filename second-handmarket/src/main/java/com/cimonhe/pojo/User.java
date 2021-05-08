package com.cimonhe.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor

@ApiModel("用户实体")
public class User {

    @ApiModelProperty("用户id")
    int id;
    @ApiModelProperty("用户名")
    String username;
    @ApiModelProperty("密码")
    String password;
    @ApiModelProperty("邮箱")
    String email;
    @ApiModelProperty("不合格商品数")
    int unqualifiedNum;
    @ApiModelProperty("是否是管理员")
    boolean isAdmin;

    Boolean gender;

    String grade;

    String sno;

    String academy;

    String major;

    String introduction;

    public User(String username, String password, String email, int unqualifiedNum, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.unqualifiedNum = unqualifiedNum;
        this.isAdmin = isAdmin;
    }


}
