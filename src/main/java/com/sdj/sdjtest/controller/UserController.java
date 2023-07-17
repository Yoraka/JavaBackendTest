package com.sdj.sdjtest.controller;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponseBody;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.sdj.sdjtest.dto.UserDTO;
import com.sdj.sdjtest.dto.UserInfoDTO;
import com.sdj.sdjtest.service.UserService;
import com.sdj.sdjtest.token.JwtToken;
import com.sdj.sdjtest.utils.JWTUtil;
import com.sdj.sdjtest.utils.SMSUtil;
import com.sdj.sdjtest.utils.StringUtil;
import com.sdj.sdjtest.vo.Result;
@CrossOrigin(exposedHeaders = "key,token")
@RestController
public class UserController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @RequestMapping("/test")
    String home() {
        return "Hello, World!";
    }
    //发送短信
    @RequestMapping("/sendSMS")
    public ResponseEntity<Result> sendSMS(String phone){
        String key = StringUtil.uuid();
        System.out.println(key);
        String code = StringUtil.randomNumber(4);
        redisTemplate.opsForValue().set(key,code,20,TimeUnit.MINUTES);
        try{
            SendSmsResponseBody sendSmsResponseBody = SMSUtil.sendSMS(phone,code);
            if(sendSmsResponseBody.getCode().equals("OK")){
                return ResponseEntity.status(200).header("key",key).body(Result.ok("发送成功"));
            } else {
                return ResponseEntity.status(200).body(Result.error("发送失败"));
            }
        } catch (ExecutionException e){
            e.printStackTrace();
            return ResponseEntity.status(200).body(Result.error("发送失败"));
        } catch (InterruptedException e){
            e.printStackTrace();
            return ResponseEntity.status(200).body(Result.error("发送失败"));
        }
    }
    //注册
    @PostMapping("reg")
    public ResponseEntity<Result> reg(@Validated @RequestBody UserDTO userDTO, @RequestHeader("key") String key){
        String code = (String) redisTemplate.opsForValue().get(key);
        if(code == null){
            return ResponseEntity.status(200).body(Result.error("验证码已过期"));
        }
        if(!code.equals(userDTO.getCode())){
            return ResponseEntity.status(200).body(Result.error("验证码错误"));
        }
        try{
            boolean success = userService.reg(userDTO);
            if(!success){
            return ResponseEntity.status(200).body(Result.error("注册失败"));
            } else {
                redisTemplate.delete(key);
                return ResponseEntity.status(200).body(Result.ok("注册成功"));
            }
        } catch (Exception e){
            return ResponseEntity.status(500).body(Result.error("手机号已存在"));
        }
        
    }
    //获取验证码
    @GetMapping("imageCode")
    public ResponseEntity<Result> imageCode(){
        String text = defaultKaptcha.createText();
        System.out.println(text);
        String uuid = StringUtil.uuid();
        System.out.println(uuid);
        redisTemplate.opsForValue().set(uuid,text,5,TimeUnit.MINUTES);
        BufferedImage image = defaultKaptcha.createImage(text);
        String image2String = StringUtil.changeImage2String(image);
        return ResponseEntity.status(200).header("key",uuid).body(Result.ok("获取成功",image2String));
    }
    //登录
    @PostMapping("login")
    public ResponseEntity<Result> login(@Validated @RequestBody UserDTO userDTO, @RequestHeader("key") String key){
        String code = (String) redisTemplate.opsForValue().get(key);
        if(code == null){
            return ResponseEntity.status(200).body(Result.error("验证码已过期"));
        }
        if(!code.equals(userDTO.getCode())){
            return ResponseEntity.status(200).body(Result.error("验证码错误"));
        }
        JwtToken jwtToken = userService.login(userDTO);
        if(jwtToken == null){
            return ResponseEntity.status(200).body(Result.error("登录失败"));
        } else {
            redisTemplate.delete(key);
            return ResponseEntity.status(200).body(Result.ok("登录成功",jwtToken));
        }
    }
    //获取用户信息
    @RequiresRoles(value={"admin","common"},logical= Logical.OR)
    @GetMapping("getUserInfo")
    public ResponseEntity<Result> getUserInfo(){
        Subject subject = SecurityUtils.getSubject();
        AuthenticationToken token = subject.getPrincipal() == null ? null : (AuthenticationToken) subject.getPrincipal();
        if(token == null){
            return ResponseEntity.status(200).body(Result.error("请先登录"));
        }
        int userId = JWTUtil.getUserId(token.getCredentials().toString());
        UserDTO userDTO = userService.getUserInfo(userId);
        return ResponseEntity.status(200).body(Result.ok("查询成功",userDTO));
    }
    //查询用户选课信息
    @RequiresRoles(value={"admin","common"},logical= Logical.OR)
    @GetMapping("getUserCourse")
    public ResponseEntity<Result> getUserCourse(@RequestParam(required = false,defaultValue = "1") Integer page,
                                                @RequestParam(required = false,defaultValue = "10") Integer limit){
        Subject subject = SecurityUtils.getSubject();
        AuthenticationToken token = subject.getPrincipal() == null ? null : (AuthenticationToken) subject.getPrincipal();
        if(token == null){
            return ResponseEntity.status(200).body(Result.error("请先登录"));
        }
        int userId = JWTUtil.getUserId(token.getCredentials().toString());
        return ResponseEntity.status(200).body(Result.ok("查询成功",userService.getUserCourse(page, limit, userId)));
    }
    //修改用户信息
    @RequiresRoles(value={"admin","common"},logical= Logical.OR)
    @PostMapping("updateUserInfo")
    public ResponseEntity<Result> updateUserInfo(@Validated @RequestBody UserInfoDTO userInfoDTO){
        Subject subject = SecurityUtils.getSubject();
        AuthenticationToken token = subject.getPrincipal() == null ? null : (AuthenticationToken) subject.getPrincipal();
        if(token == null){
            return ResponseEntity.status(200).body(Result.error("请先登录"));
        }
        int userId = JWTUtil.getUserId(token.getCredentials().toString());
        boolean success = userService.updateUserInfo(userInfoDTO);
        if(!success){
            return ResponseEntity.status(200).body(Result.error("修改失败"));
        } else {
            return ResponseEntity.status(200).body(Result.ok("修改成功"));
        }
    }
    //选课
    @RequiresRoles(value={"admin","common"},logical= Logical.OR)
    @PostMapping("signToCourse")
    public ResponseEntity<Result> signToCourse(Long courseId){
        Subject subject = SecurityUtils.getSubject();
        AuthenticationToken token = subject.getPrincipal() == null ? null : (AuthenticationToken) subject.getPrincipal();
        if(token == null){
            return ResponseEntity.status(200).body(Result.error("请先登录"));
        }
        boolean success = userService.signToCourse(courseId);
        if(!success){
            return ResponseEntity.status(200).body(Result.error("选课失败"));
        } else {
            return ResponseEntity.status(200).body(Result.ok("选课成功"));
        }
    }
    //退课
    @RequiresRoles(value={"admin","common"},logical= Logical.OR)
    @PostMapping("signOutCourse")
    public ResponseEntity<Result> signOutCourse(Long courseId){
        Subject subject = SecurityUtils.getSubject();
        AuthenticationToken token = subject.getPrincipal() == null ? null : (AuthenticationToken) subject.getPrincipal();
        if(token == null){
            return ResponseEntity.status(200).body(Result.error("请先登录"));
        }
        boolean success = userService.signOutCourse(courseId);
        if(!success){
            return ResponseEntity.status(200).body(Result.error("退课失败"));
        } else {
            return ResponseEntity.status(200).body(Result.ok("退课成功"));
        }
    }
    //修改密码
    @PostMapping("updatePassword")
    public ResponseEntity<Result> updatePassword(@Validated @RequestBody UserDTO userDTO, @RequestHeader("key") String key){
        String code = (String) redisTemplate.opsForValue().get(key);
        if(code == null){
            return ResponseEntity.status(200).body(Result.error("验证码已过期"));
        }
        if(!code.equals(userDTO.getCode())){
            return ResponseEntity.status(200).body(Result.error("验证码错误"));
        }
        boolean success = userService.updatePassword(userDTO.getPhone(), userDTO.getPassword());
        if(!success){
            return ResponseEntity.status(200).body(Result.error("修改失败"));
        } else {
            return ResponseEntity.status(200).body(Result.ok("修改成功"));
        }
    }
    //退出登录
    @GetMapping("logout")
    public ResponseEntity<Result> logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return ResponseEntity.status(200).body(Result.ok("退出成功"));
    }
}
