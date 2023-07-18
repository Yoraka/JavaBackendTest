package com.sdj.sdjtest.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

public class MyAuthenticationFilter extends FormAuthenticationFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (request instanceof HttpServletRequest) {
            if (((HttpServletRequest) request).getMethod().toUpperCase().equals("OPTIONS")) {
                return true;
            }
        }
        return super.isAccessAllowed(request, response, mappedValue);
    }
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
        throws Exception {
            WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
    }
}