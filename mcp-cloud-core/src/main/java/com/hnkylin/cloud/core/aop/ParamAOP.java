package com.hnkylin.cloud.core.aop;

import com.hnkylin.cloud.core.config.ParamterCheckCompent;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ParamAOP {

    @Autowired
    private ParamterCheckCompent paramterCheckComp;

    @Pointcut("@annotation(com.hnkylin.cloud.core.annotation.ParamCheck)")
    public void check() {

    }

    @Around(value = "check()")
    public Object doBefore(JoinPoint joinPoint) throws Throwable {
        Object object = null;
        // 参数校验，未抛出异常表示验证OK
        paramterCheckComp.checkAnnoValid(joinPoint.getSignature().getName(), joinPoint.getTarget(),
                joinPoint.getArgs());
        object = ((ProceedingJoinPoint) joinPoint).proceed();
        return object;
    }

}
