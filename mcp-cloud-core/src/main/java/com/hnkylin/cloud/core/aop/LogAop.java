/**
 * Copyright © 2018 com.yishouapp All rights reserved.
 *
 * @Package: com.yishou.pss.api.aop
 * @author: wanglei
 * @date: 2018年5月24日 上午11:28:59
 */
package com.hnkylin.cloud.core.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


@Aspect
@Component
@Slf4j
public class LogAop {

    @Autowired
    private ObjectMapper objectMapper;


    @Pointcut("execution(* com.hnkylin.cloud.*.ctrl..*(..))")
    public void webLog() {
    }

    private static final String LOG = "url:{}  ,params: {}";


    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Object[] args = joinPoint.getArgs();
        //过滤后序列化无异常
        log.info(LOG, request.getRequestURI(), ArrayUtils.toString(args));
    }


    @Around("webLog()")
    public Object doAroud(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        //打印出参
        log.info("Response Args : {}，Time-Consuming：{}", objectMapper.writeValueAsString(result),
                System.currentTimeMillis() - startTime);

        return result;
    }

}
