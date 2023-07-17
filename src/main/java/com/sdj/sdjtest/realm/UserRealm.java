package com.sdj.sdjtest.realm;

import com.sdj.sdjtest.entity.SysUser;
import com.sdj.sdjtest.mapper.UserMapper;
import com.sdj.sdjtest.service.UserService;
import com.sdj.sdjtest.token.JwtToken;
import com.sdj.sdjtest.utils.JWTUtil;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
 
/**
 * @Author : JCccc
 * @CreateTime : 2020/4/24
 * @Description :
 **/
public class UserRealm extends AuthorizingRealm {
 
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取登录用户名
        String token = (String) principalCollection.getPrimaryPrincipal();
        int userId = JWTUtil.getUserId(token);
        //添加角色和权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
 
        List<Map<String, Object>> powerList = userService.getUserPower(userId);
        System.out.println(powerList.toString());
        for (Map<String, Object> powerMap : powerList) {
            //添加角色
            simpleAuthorizationInfo.addRole(String.valueOf(powerMap.get("roleName")));
            //添加权限
            simpleAuthorizationInfo.addStringPermission(String.valueOf(powerMap.get("permissionsName")));
        }
        return simpleAuthorizationInfo;
    }
 
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //加这一步的目的是在请求的时候会先进认证，然后再到接口
        if (authenticationToken.getPrincipal() == null) {
            return null;
        }
        //获取用户信息
        String token = authenticationToken.getPrincipal().toString();
        //根据用户名去查询用户信息
        int userId = JWTUtil.getUserId(token);
        if(userId == 0 || !JWTUtil.verify(token)){
            throw new AuthenticationException("token认证失败！");
        }
        //redis
        Object redisToken = redisTemplate.opsForValue().get("token_" + userId);
        if(redisToken == null || !redisToken.equals(token)){
            throw new AuthenticationException("token认证失败！");
        }
        return new SimpleAuthenticationInfo(token, token, "my_realm");
    }
}
