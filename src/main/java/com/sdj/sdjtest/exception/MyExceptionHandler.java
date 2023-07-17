package com.sdj.sdjtest.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
 
/**
 * @Author : JCccc
 * @CreateTime : 2020/4/24
 * @Description :
 **/
@ControllerAdvice
@Slf4j
public class MyExceptionHandler {
 
    @ExceptionHandler
    @ResponseBody
    public String ErrorHandler(AuthorizationException e) {
        log.error("权限校验失败！", e);
        return "您暂时没有权限,请联系管理员！";
    }
}