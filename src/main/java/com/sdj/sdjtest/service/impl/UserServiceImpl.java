package com.sdj.sdjtest.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.websocket.OnError;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sdj.sdjtest.dto.UserDTO;
import com.sdj.sdjtest.dto.UserInfoDTO;
import com.sdj.sdjtest.entity.Course;
import com.sdj.sdjtest.entity.SysUser;
import com.sdj.sdjtest.mapper.UserMapper;
import com.sdj.sdjtest.service.UserService;
import com.sdj.sdjtest.token.JwtToken;
import com.sdj.sdjtest.utils.JWTUtil;
import com.sdj.sdjtest.utils.MD5Util;
import com.sdj.sdjtest.utils.StringUtil;
import com.sdj.sdjtest.vo.CourseUserVO;
import com.sdj.sdjtest.vo.CourseVO;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public boolean reg(UserDTO userDTO){
        SysUser Quser = userMapper.selectUserByPhone(userDTO.getPhone());
        if(Quser != null){
            throw new RuntimeException("手机号已注册");
        }
        String salt = StringUtil.randomNumber(6);

        SysUser user = new SysUser();
        //user.setUserId(1L);
        user.setPhone(userDTO.getPhone());
        user.setPassword(userDTO.getPassword());
        String md5 = MD5Util.getMD5(userDTO.getPassword(), salt, 10);
        user.setPassword(md5);
        user.setSalt(salt);
        user.setNickname("nickname");
        user.setState(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        int insert = userMapper.insertUser(user);
        if(insert >= 1){
            return true;
        } else {
            return false;
        }
    }
    @Override
    public JwtToken login(UserDTO userDTO){
        //添加用户认证信息
        Subject subject = SecurityUtils.getSubject();
        String phone = userDTO.getPhone();
        String password = userDTO.getPassword();
        SysUser user = userMapper.selectUserByPhone(phone);
        String salt = user.getSalt();
        String md5 = MD5Util.getMD5(password, salt, 10);
        if(!md5.equals(user.getPassword())){
            throw new RuntimeException("手机号或密码错误");
        }
        JwtToken jwtToken = new JwtToken(JWTUtil.sign(user.getUserId()));
        redisTemplate.opsForValue().set("token_" + user.getUserId(), JWTUtil.sign(user.getUserId()), 60, TimeUnit.MINUTES);
        try {
            //进行验证，AuthenticationException可以catch到,但是AuthorizationException因为我们使用注解方式,是catch不到的,所以后面使用全局异常捕抓去获取
            subject.login(jwtToken);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new RuntimeException("手机号或密码错误");
        } catch (AuthorizationException e) {
            e.printStackTrace();
            throw new RuntimeException("没有权限");
        }
        return jwtToken;
    }

    @Override
    public boolean updatePassword(String phone, String newPwd){
        try{
            SysUser user = userMapper.selectUserByPhone(phone);
            if(user == null){
                throw new RuntimeException("账号不存在");
            }
            String salt = user.getSalt();//数据库原有salt
            String newMd5 = MD5Util.getMD5(newPwd, salt, 10);
            return userMapper.updatePassword(phone, newMd5) > 0;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> getUserPower(int userId){
        return userMapper.getUserPower(userId);
    }

    @Override
    public boolean signToCourse(Long courseId){
        Subject subject = SecurityUtils.getSubject();
        String token = (String) subject.getPrincipal();
        int userId = JWTUtil.getUserId(token);
        return userMapper.signToCourse(userId, courseId) > 0;
    }
    @Override
    public boolean signOutCourse(Long courseId){
        Subject subject = SecurityUtils.getSubject();
        String token = (String) subject.getPrincipal();
        int userId = JWTUtil.getUserId(token);
        return userMapper.signOutCourse(userId, courseId) > 0;
    }
    @Override
    public UserDTO getUserInfo(int userId){
        return userMapper.getUserInfo(userId);
    }
    @Override
    public PageInfo<Course> getUserCourse(Integer page, Integer limit, int userId){
        PageHelper.startPage(page, limit);
        List<Course> list = userMapper.getUserCourse(userId);
        PageInfo<Course> pageInfo = new PageInfo<>(list);
        list.stream().map(course -> {
            CourseVO courseVO = new CourseVO();
            BeanUtils.copyProperties(course, courseVO);
            return courseVO;
        }).collect(Collectors.toList());
        return pageInfo;
    }
    @Override
    public boolean updateUserInfo(UserInfoDTO userInfoDTO){
        SysUser user = new SysUser();
        user.setNickname(userInfoDTO.getNickName());
        user.setSex(userInfoDTO.getSex());
        user.setBirthday(userInfoDTO.getBirthday());
        user.setAddress(userInfoDTO.getAddress());
        user.setUpdateTime(new Date());
        return userMapper.updateUserInfo(user) > 0;
    }
}