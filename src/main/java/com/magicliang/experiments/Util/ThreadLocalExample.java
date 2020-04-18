package com.magicliang.experiments.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by magicliang on 2016/6/26.
 */
public class ThreadLocalExample {
    private static final Logger log = LoggerFactory.getLogger(ThreadLocalExample.class);

    private static final AtomicInteger atomicNum = new AtomicInteger(0);
    //这里不能用lambda，因为不是functional interface
    private static final ThreadLocal<Integer> threadLocalAtomicNum = new ThreadLocal<Integer>() {//内部匿名类不像容器类，不能自动做类型推导
        @Override
        protected Integer initialValue() {//这个函数是每个线程会单独执行一次的，执行出来的结果值才是独立存储的
            return atomicNum.getAndIncrement();
        }
    };

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //值得注意的是，线程池只有10个线程，所以只会初始化10个变量，即使有16个runnable，也没有16个值
        for (int i = 0; i < 16; i++) {
            executorService.execute(() -> {
                log.info("The thread is: " + Thread.currentThread() + ", the num is:" + threadLocalAtomicNum.get());
            });
        }

        executorService.shutdown();
        if (!executorService.awaitTermination(1L, TimeUnit.MINUTES)) {
            executorService.shutdownNow();
        }
    }
}
