package com.magicliang.experiments.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
join 是一对一的协同
countdownlatch是多对一的协同
cyclicbarrier是加强型的countdownlatch
wait是基础的，最自由的协同
* */

/**
 * Created by magicliang on 2016/6/26.
 */
public class SomeSyncTest {
    private static final Logger log = LoggerFactory.getLogger(SomeSyncTest.class);
    private static volatile int flag1 = 0;
    private static volatile boolean shutdown = false;
    private static final Object lock1 = new Object();
    private static final Object lock2 = new Object();

    static CountDownLatch c = new CountDownLatch(2);


    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService1 = Executors.newFixedThreadPool(5);
        //Join 一个针对线程的wait
        Thread t1 = new Thread(() -> {
            try {
                log.info("t1 begin");
                Thread.sleep(2000L);//测试发现原来sleep不准，即使是毫秒级的都不准，当然也许和log实现有关
                log.info("t1 over");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                log.info("t2 begin");
                Thread.sleep(2000L);
                log.info("t2 over");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.info("main thread go to check point 1");

        //其他实现：在executor 里面，使用invokeAll或者对future使用get等阻塞api来使主线程阻塞

        executorService1.execute(() -> {
            log.info("t3 begin");
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            synchronized (lock1) {
                flag1++;
                lock1.notify();
                log.info("t3 over, release lock1");
            }
        });
        executorService1.execute(() -> {
            log.info("t4 begin");
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            synchronized (lock1) {
                flag1++;
                lock1.notify();
                log.info("t4 over, release lock1");//如果放在锁之外，有可能输出反而比t5晚
            }
        });
        executorService1.execute(() -> {
            log.info("t5 begin");
            synchronized (lock1) {
                while (flag1 != 2) {
                    try {
                        shutdown = false;
                        //理论上，进入wait以后，应该放弃锁了
                        log.info("flag1 not ready, t5 release lock1");
                        lock1.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.info("Being interrupt, need to stop incomplete t5");
                        break;//但在while里面，如果被打断还要继续跑循环，可能永远不会释放锁。
                    }
                }
                log.info("t5 release lock1");
            }
            flag1 = 0;
            log.info("t5 finnally over");
            shutdown = true;
        });
//多个线程控制shutdown太繁琐了
//        executorService1.execute(() -> {
//            log.info("t6 begin");
//            synchronized (lock1) {
//                try {
//                    while (flag1 != 2) {
//                        shutdown = false;
//                        //理论上，进入wait以后，应该放弃锁了
//                        log.info("flag1 not ready, t6 release lock1");
//                        lock1.wait();
//                    }
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    log.info("Being interrupt, need to stop incomplete t6");
//                }
//                log.info("t6 release lock1");
//            }
//            flag1 = 0;
//            log.info("t5 finnally over");
//            shutdown = true;
//        });
        //所以如果在锁没有被全部释放完的情况下强行地executorService.shutdownNow()，就会不断地打断wait，又在循环里重新wait
        //其他等待锁的线程永远饥饿，而且检测不到死锁
        //不应该使用这个变量来操纵线程池，应该使用while 中的 break，或者其他同步设施
        while (!shutdown) {
            //log.info("unable to shutdown thread pool");
        }
        log.info("ready  to shutdown thread pool 1");
        //一个问题，怎样才能让线程全部执行完，才关闭线程池？
        //在现实中似乎是不可能的，因为有些io线程，总是倾向于使用无限等待
        //长等待的线程，只能接受条件不完整而被break的结局，而不能接受条件满足跳出while的结局
        executorService1.shutdown();
        if (!executorService1.awaitTermination(1L, TimeUnit.NANOSECONDS)) {
            executorService1.shutdownNow();//这里会无限地打断waiting的线程
        }
        log.info("thread pool 1 shutdowned");

        ExecutorService executorService2 = Executors.newFixedThreadPool(5);

        executorService2.execute(() -> {
            log.info("t7 begin");
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            synchronized (lock2) {
                c.countDown();
            }
        });

        executorService2.execute(() -> {
            log.info("t8 begin");
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            synchronized (lock2) {
                c.countDown();
            }
        });
        //主线程等到countdown被其他线程搞定，才关闭，这样也可以保证任务完成
        c.await();
        executorService2.shutdown();
        if (!executorService2.awaitTermination(1L, TimeUnit.NANOSECONDS)) {
            executorService2.shutdownNow();//这里会无限地打断waiting的线程
        }
        log.info("thread pool 2 shutdowned");
    }
}
