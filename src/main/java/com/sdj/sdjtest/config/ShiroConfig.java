package com.sdj.sdjtest.config;

import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sdj.sdjtest.filter.MyAuthenticationFilter;
import com.sdj.sdjtest.filter.OptionalFilter;
import com.sdj.sdjtest.realm.UserRealm;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;
 
/**
 * @Author : JCccc
 * @CreateTime : 2020/4/24
 * @Description :
 **/
@Configuration
public class ShiroConfig {
    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
        defaultAAP.setProxyTargetClass(true);
        return defaultAAP;
    }
 
    //将自己的验证方式加入容器
    @Bean
    public UserRealm myShiroRealm() {
        UserRealm userRealm = new UserRealm();
        return userRealm;
    }
 
    //权限管理，配置主要是Realm的管理认证
    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myShiroRealm());
        return securityManager;
    }
 
    //对url的过滤筛选
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        
        
        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        filters.put("authc", new OptionalFilter());
        shiroFilterFactoryBean.setFilters(filters);


        Map<String, String> map = new HashMap<>();
        map.put("/updatePassword","anon");
        map.put("/sendSMS","anon");
        map.put("/reg","anon");
        map.put("/imageCode","anon");
        //登出
        map.put("/logout", "anon");
        //对所有用户认证
        map.put("/**", "authc");
        //登录
        shiroFilterFactoryBean.setLoginUrl("/login");
        // Map<String, Filter> filterMap = shiroFilterFactoryBean.getFilters();
        // filterMap.put("authc", new MyAuthenticationFilter());

        //成功登录后跳转的url
        //shiroFilterFactoryBean.setSuccessUrl("/xxxx");
 
 
        //错误页面，认证不通过跳转
        // shiroFilterFactoryBean.setUnauthorizedUrl("/error");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }
 
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}