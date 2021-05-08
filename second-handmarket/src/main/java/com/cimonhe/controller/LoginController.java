package com.cimonhe.controller;

import com.alibaba.fastjson.JSONObject;
import com.cimonhe.constant.ReturnValueConstant;
import com.cimonhe.pojo.User;
import com.cimonhe.service.MailService;
import com.cimonhe.service.MyUserDetailsService;
import com.cimonhe.service.UserService;
import com.cimonhe.utils.JwtUtil;
import com.cimonhe.utils.MailVo;
import com.cimonhe.utils.RSAEncrypt;
import com.cimonhe.utils.URLFileMap;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/login")
@CrossOrigin
@Transactional
public class LoginController {

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Value("${auth.privateKey}")
    private String privateKey ;
    @Value("${auth.publicKey}")
    private String publicKey ;


    @ApiOperation("密码登录接口")
    @ApiResponses({
            @ApiResponse(code=200,message="userId:用户id username:用户名 email:邮箱 isAdmin:是否是管理员（boolean）,portraitPath:头像存储路径, jwt : token"),
    })
    @PostMapping(value = "/loginByPwd",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userInfo", value = "用户信息（邮箱|用户名）", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "encryptPassword", value = "用户加密密码", required = true, dataType = "String",paramType="query")
    })
    public String loginByPwd(@RequestParam String userInfo,@RequestParam String encryptPassword) throws Exception
    {
        JSONObject returnValue = new JSONObject();
        String password = RSAEncrypt.decrypt(encryptPassword,privateKey);
        User user = userService.queryUserByName(userInfo);
        if (user==null)
            user = userService.queryUserByEmail(userInfo);
        if (user==null)
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"登录失败，无该账号!");
        }
        else if (redisTemplate.hasKey("loginLimit"+user.getId())&&(((int) redisTemplate.opsForValue().get("loginLimit"+user.getId()))==15))
        {

            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"登录失败，尝试密码的次数过多请半小时后重试!");
        }
        else if (!passwordEncoder.matches(password,user.getPassword()))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"登录失败，密码出错!");
            if (!redisTemplate.hasKey("loginLimit"+user.getId()))
                redisTemplate.opsForValue().set("loginLimit"+user.getId(),0);
            if (redisTemplate.opsForValue().increment("loginLimit"+user.getId(),1)==5)
            {
                Duration duration = Duration.between(LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));
                redisTemplate.expire("loginLimit"+user.getId(),duration.toMinutes(), TimeUnit.MINUTES);
            }
        }
        else
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"登录成功!");
            returnValue.put("userId",user.getId());
            returnValue.put("username", user.getUsername());
            returnValue.put("email",user.getEmail());
            returnValue.put("isAdmin",user.isAdmin());
            returnValue.put("portraitPath", URLFileMap.getPortraitImgPath(user.getId()));
            String tokenVersion = "tokenVersion"+user.getId();
            String tokenVersionUUID = UUID.randomUUID().toString().replace("-", "").toLowerCase();
            final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            if (!redisTemplate.hasKey(tokenVersion))
                redisTemplate.opsForValue().set(tokenVersion,tokenVersionUUID);
            else
                tokenVersionUUID = (String) redisTemplate.opsForValue().get(tokenVersion);
            System.err.println(tokenVersionUUID);
            Map<String,Object> claims = new HashMap<>();
            claims.put("tokenVersion",tokenVersionUUID);
            returnValue.put("jwt",jwtTokenUtil.createToken(claims,userDetails.getUsername()));
        }
        return returnValue.toString();
    }

    @ApiOperation("返回密码加密公钥")
    @ApiResponses({
            @ApiResponse(code=200,message="publicKeyString: 公钥"),
    })
    @PostMapping(value = "/getPublicKeyString",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
    })
    public String getPublicKeyString() {
        return publicKey;
    }

    @ApiOperation("通过验证邮箱找回用户密码")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/findPwd",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newEncryptPwd", value = "新加密密码", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "verifyCode", value = "验证码", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "email", value = "邮箱", required = true, dataType = "String",paramType="query"),

    })
    public String findPwd(HttpServletRequest request,@RequestParam  String newEncryptPwd,@RequestParam  String email,@RequestParam  String verifyCode) throws Exception {
        JSONObject returnValue = new JSONObject();
        String newPwd = RSAEncrypt.decrypt(newEncryptPwd,privateKey);
        User user = userService.queryUserByEmail(email);
        String findPwdCode =(String) request.getSession().getAttribute("findPwdCode");
        String findPwdEmail =(String) request.getSession().getAttribute("findPwdEmail");
        if (!email.equals(findPwdEmail))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"还未向该邮箱发送验证码或者该邮箱验证码已过期");
        }
        else if (passwordEncoder.matches(newPwd,user.getPassword()))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"新密码不应该与原密码相同");
        }
        else if (!findPwdCode.equals(verifyCode))
        {
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"验证码输入错误");
        }
        else
        {
            userService.upPasswordById(user.getId(),passwordEncoder.encode(newPwd));
            returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.SUCCESS);
            returnValue.put(ReturnValueConstant.MSG,"修改密码成功");
            request.getSession().removeAttribute("findPwdCode");
            redisTemplate.delete("tokenVersion"+user.getId());
        }
        return returnValue.toString();
    }

    @ApiOperation("发送修改密码邮箱验证码")
    @ApiResponses({
            @ApiResponse(code=200,message="无返回值"),
    })
    @PostMapping(value = "/sendFindPwdMail",produces = { "application/json;charset=UTF-8" })
    @ResponseBody
    @ApiImplicitParams({

    })
    public String sendFindPwdMail(HttpServletRequest request,@RequestParam String email) {
        JSONObject returnValue = new JSONObject();
        MailVo mailVo = new MailVo();
        String upPwdVerifyCode = String.valueOf(((int)((Math.random() * 9 + 1) * 100000)));
        mailVo.setSubject("易物二手市场找回密码邮箱验证码");
        mailVo.setText("验证码为"+upPwdVerifyCode);
        mailVo.setTo(email);
        request.getSession().removeAttribute("findPwdEmail");
        request.getSession().setAttribute("findPwdEmail",email);
        request.getSession().removeAttribute("findPwdCode");
        request.getSession().setAttribute("findPwdCode",upPwdVerifyCode);
        mailService.sendMail(mailVo);//发送邮件和附件
        returnValue.put(ReturnValueConstant.STATUS, ReturnValueConstant.SUCCESS);
        returnValue.put(ReturnValueConstant.MSG,"发送验证码成功");
        return returnValue.toString();
    }

}
