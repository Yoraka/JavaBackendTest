package com.sdj.sdjtest.filter;

import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//使用authc进行权限控制时，若认证不通过，则shiro默认会重定向到loginUrl路径，前端请求接口时会出现302错误
//CORS跨域请求有时发送请求时会预先发送一个OPTIONS请求，不会写到token和参数，这就导致shiro拦截到请求后判定当前用户未登录，从而引发问题
public class OptionalFilter extends UserFilter {

    /**
     * 在访问过来的时候检查是否为OPTIONS请求，如果是就直接返回true
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        //处理option请求
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if(httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())){
            return true;
        }
        //单点登录
        HttpServletRequest httpRequestH = (HttpServletRequest) request;
        String token = httpRequestH.getHeader("Authorization");
        Subject subject = getSubject(request, response);
        //如果 isAuthenticated 为 false 证明不是登录过的，同时 isRemembered 为true 证明是没登陆直接通过记住我功能进来的
        if(!subject.isAuthenticated()  && subject.isRemembered()){
            Session session = subject.getSession();
            if(session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY)!=null){
                subject.logout();
            }
        }
        return super.preHandle(httpRequest,httpResponse);
    }

    /**
     * 表示访问拒绝时是否自己处理，如果返回true表示自己不处理且继续拦截执行，返回false表示自己已经处理了
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

        Subject subject = getSubject(request, response);
        if (subject.isAuthenticated()) {
            System.out.println("YES");
            return true;
        }
        else {
            System.out.println("NO");
            // response.setContentType("application/json;charset=UTF-8");
            // JSONObject jsonObject = new JSONObject();
            // jsonObject.put("code", 4020);
            // jsonObject.put("message", "未登录!");
            // HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            // HttpServletRequest httpRequest = (HttpServletRequest) request;
            // httpServletResponse.setHeader("Access-control-Allow-Origin", httpRequest.getHeader("Origin"));
            // httpServletResponse.setHeader("Access-Control-Allow-Methods", httpRequest.getMethod());
            // httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            // httpServletResponse.setHeader("Access-Control-Allow-Headers", httpRequest.getHeader("Access-Control-Request-Headers"));
            // response = httpServletResponse;
            // response.getWriter().write(jsonObject.toJSONString());
            return true;
    //        return super.onAccessDenied(request, response);
        }

    }
}
