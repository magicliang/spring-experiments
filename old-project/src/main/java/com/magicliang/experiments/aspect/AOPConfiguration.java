package com.magicliang.experiments.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created by magicliang on 2016/3/19.
 */
@Aspect
@Component
@Order(0)
@Slf4j
public class AOPConfiguration {

    public AOPConfiguration() {
        log.info("AOP beginning!!!!!!!!!!!!!!!!!!!!!!!");
    }

    //
    //@Pointcut("execution(* ab*(..))")
    @Pointcut("execution(public String abc(..))")
    public void executeABC() {
        //this method will never be invoked.
        log.info("pointcut abc");
    }

    @Around("executeABC()")
    //@Before("execution(public String executeABC())")
    private Object aroundMethod(ProceedingJoinPoint thisJoinPoint) {
        log.info("切面abc执行了。。。。");
        try {
            String s = (String) thisJoinPoint.proceed();
            log.info("response is: " + s);
            return s;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    @AfterReturning("execution(* com..HelloController.*(..))")
    private void afterControllerMethodReturning(JoinPoint joinPoint) {
        log.info("@AfterReturning: " + joinPoint);
    }

    @AfterReturning("execution(* com.example.controller..*.*(..))")
    private void anyPublicOperation() {
        log.info("public operation ");
    }
    //@Before("com.example.controller.HelloController.abc()")
    //public void BeforeABC() {
    //log.info("before abc");
    //}

}
