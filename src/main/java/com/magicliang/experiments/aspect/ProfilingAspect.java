package com.magicliang.experiments.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * Created by magicliang on 2016/4/2.
 */
@Slf4j
@Aspect
@Component
public class ProfilingAspect {

    @Around("methodsToBeProfiled()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable{
        StopWatch sw = new StopWatch(getClass().getSimpleName());
        try {
            sw.start(pjp.getSignature().getName());
            return pjp.proceed();
        }
        finally {
            sw.stop();
            log.info(sw.prettyPrint());
        }
    }

    @Pointcut("execution(public String abc(..))")
    public void methodsToBeProfiled(){}
}
