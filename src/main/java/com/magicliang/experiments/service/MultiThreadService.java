package com.magicliang.experiments.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * Created by magicliang on 2016/6/24.
 */
@Service
public class MultiThreadService {
    private static final Logger log = LoggerFactory.getLogger(MultiThreadService.class);

    MultiThreadService() {
    }

    @PostConstruct
    private void postConstruct() {
        testCompletableFuture();
    }

    private void testCompletableFuture() {
        final ExecutorService pool = Executors.newFixedThreadPool(10);
        //这方法本身是自带线程池的
        //问题是supplyAsync()默认使用 ForkJoinPool.commonPool()，线程池由所有的CompletableFutures分享，所有的并行流和所有的应用都部署在同一个虚拟机上(如果你很不幸的仍在使用有很多人工部署的应用服务器)。这种硬编码的，不可配置的线程池完全超出了我们的控制，很难去监测和度量。
        final CompletableFuture<String> downloading = CompletableFuture.supplyAsync(() -> {
//            try (InputStream is = new URL("http://www.baidu.com").openStream()) {
//                //这是一个内部线程
//                log.info("Downloading");
//                return IOUtils.toString(is, "UTF-8");
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//                throw new RuntimeException(e);
//            } catch (IOException e) {
//                e.printStackTrace();
//                throw new RuntimeException(e);
//            }
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            //Use abc instead
            return "ABC";

            //也可以额外提供一个线程池
        }, pool);
        while (!downloading.isDone()) {
            //这是在主线程里，所以有可能反而输入在前
            log.info("Still Downloading");
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                //被interrupt打断，用interrupt复位。
                Thread.currentThread().interrupt();
            }
        }
        try {
            log.info("The download value is: " + downloading.get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //这这里注解了线程池的手动销毁，但线程池总要销毁的
        //pool.shutdownNow();
//        try {
//            pool.awaitTermination(1, TimeUnit.MINUTES);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        //两个链式调用都像promise a+一样，如果状态是resolved的，会立即执行
        //第一个链式调用
        downloading.thenApply(s -> {
            //不管怎么测试，都是主线程，为什么？
            log.info("Even the pool is shutdowned, first transformation can still be applied");
            return s.length();
        });
        //第二个链式调用
        downloading.thenApplyAsync(s -> {
            //这个线程大概是在CompletableFuture内部的线程池执行的
            //推测，所有的async方法都是在内部线程池执行的
            log.info("Even the pool is shutdowned, second transformation can still be applied");
            return s.length();
        });
        //它可以期待一个返回值是completableFuture，类似promise里接promise
        downloading.thenCompose(this::strLen);
    }

    private CompletableFuture<Integer> strLen(String s) {
        //supplyAsync不能用参数（callable runnable？）所以这里闭包了
        //但其他的 async方法可以自由搭配参数
        return CompletableFuture.supplyAsync(s::length);
    }

}
