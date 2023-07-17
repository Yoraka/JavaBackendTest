package com.sdj.sdjtest.filter;

import java.io.PrintWriter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.filter.authc.AuthenticationFilter;

public class RestFilter extends AuthenticationFilter {

    public final String LOGIN_REQUIRED_RESPONSE = "{\"status\" : 1}";

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        PrintWriter out = servletResponse.getWriter();
        servletResponse.setContentType("application/json;charset=utf-8");
        servletResponse.setCharacterEncoding("UTF-8");
        out.print(LOGIN_REQUIRED_RESPONSE);
        out.flush();

        return false;
    }

}

