package com.magicliang.experiments.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.StopWatch;

/**
 * project name: spring-experiments
 * <p>
 * description:
 *
 * @author magicliang
 * <p>
 * date: 2020-04-18 23:28
 */
@Slf4j
// 这个注解可有可无
// @ConfigurationProperties("interceptor")
@Aspect
public class ProfilingAspect {

    @Around("methodsToBeProfiled()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch sw = new StopWatch(getClass().getSimpleName());
        try {
            sw.start(pjp.getSignature().getName());
            return pjp.proceed();
        } finally {
            sw.stop();
            log.info("time:" + sw.prettyPrint());
        }
    }

    @Pointcut("execution(public * com.magicliang..*.*(..))")
    public void methodsToBeProfiled() {
    }
}