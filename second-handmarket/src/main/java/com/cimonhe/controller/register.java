package com.cimonhe.controller;

import com.alibaba.fastjson.JSONObject;
import com.cimonhe.constant.ReturnValueConstant;
import com.cimonhe.pojo.User;
import com.cimonhe.service.MailService;
import com.cimonhe.service.UserService;
import com.cimonhe.utils.MailVo;
import com.cimonhe.utils.RSAEncrypt;
import com.cimonhe.utils.URLFileMap;
import com.cimonhe.utils.ValidateUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

@RestController
@RequestMapping("/register")
@CrossOrigin
@Transactional
public class register {

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${auth.privateKey}")
    private String privateKey ;

    @ApiOperation("注册接口")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/reg",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "邮箱", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "password", value = "用户密码", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "verifyCode", value = "验证码", required = true, dataType = "String",paramType="query"),

    })
    public String reg(HttpServletRequest request,@RequestParam("email") String email, @RequestParam("verifyCode") String verifyCode, @RequestParam("username") String username, @RequestParam("encryptPassword") String encryptPassword) throws Exception {
        JSONObject returnValue = new JSONObject();
        String password = RSAEncrypt.decrypt(encryptPassword,privateKey);
        if (ValidateUtil.validateEmail(username))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"用户名不应该为邮箱格式");
            return returnValue.toString();
        }
        else if (!ValidateUtil.validateEmail(email))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"请输入正确格式的邮箱");
            return returnValue.toString();
        }
        else if (userService.queryUserByName(username)!=null)
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"该用户名已被他人注册，请尝试其他的名字");
            return returnValue.toString();
        }
        if (userService.queryUserByEmail(email)!=null)
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"该邮件已被他人注册，请尝试其他的邮件");
            return returnValue.toString();
        }
        if (password.equals(""))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"密码不应为空");
            return returnValue.toString();
        }

        int verificationCode =(int) request.getSession().getAttribute("verificationCode");
        if (!email.equals(request.getSession().getAttribute("email")))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"此邮箱非发送验证码的邮箱");
            return returnValue.toString();
        }

        System.err.println(verificationCode);
        System.err.println(verifyCode);
        System.out.println(String.valueOf(verificationCode).equals(verifyCode));
        if (String.valueOf(verificationCode).equals(verifyCode)) //接受用户输入的验证码并判断是否成功
        {
            User user = new User(username,passwordEncoder.encode(password),email,0,false);
            userService.addUser(user);
            request.getSession().removeAttribute("verificationCode");
            request.getSession().removeAttribute("email");
            System.out.println("成功");
            URLFileMap.defaultPortrait(user.getId());
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.SUCCESS);
            returnValue.put("msg","注册成功！");
        }
        else
        {
            System.out.println("失败");
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put("msg","验证码输入错误");
        }
        return returnValue.toString();
    }

    @ApiOperation("发送注册邮箱验证码")
    @ApiResponses({
    })
    @PostMapping(value = "/sendMail",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "邮箱", required = true, dataType = "String",paramType="query"),

    })
    public String sendMail(HttpServletRequest request,@RequestParam String email) {
        JSONObject returnValue = new JSONObject();
        MailVo mailVo = new MailVo();
        int verificationCode = (new Random()).nextInt(1000000);
        mailVo.setSubject("易物二手市场注册验证码");
        mailVo.setText("验证码为"+verificationCode);
        mailVo.setTo(email);
        request.getSession().removeAttribute("verificationCode");
        request.getSession().removeAttribute("email");
        request.getSession().setAttribute("verificationCode",verificationCode);
        request.getSession().setAttribute("email",email);
        mailService.sendMail(mailVo);//发送邮件和附件
        returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"发送验证码成功");
        return returnValue.toString();
    }

}
