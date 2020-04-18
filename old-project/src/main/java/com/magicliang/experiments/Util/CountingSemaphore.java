package com.magicliang.experiments.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by magicliang on 2016/6/25.
 */
public class CountingSemaphore {
    private int bound = 0;
    private int signals = 0;
    private static final Logger log = LoggerFactory.getLogger(CountingSemaphore.class);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountingSemaphore countingSemaphore = new CountingSemaphore(5);

        for (int i = 0; i < 5; i++) {
            executor.execute(() -> {
                //因为是在其他线程里执行，所以主线程throws出去也没用
                try {
                    countingSemaphore.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        Thread.sleep(1000L);
        for (int i = 0; i < 5; i++) {
            executor.execute(() -> {
                try {
                    countingSemaphore.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
//        for (int i = 0; i < 5; i++) {
//            executor.execute(() -> {
//                try {
//                    countingSemaphore.take();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        countingSemaphore.release();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }

        //If we don't shutdown the thread pool, the pool will hold the thread, and the main thread will never be reminated.
        //executor.shutdown();
        executor.shutdownNow();//(ShutdownNow will terminate the thread, no matter what status it is, even the semaphore is still waiting to be released?)
    }

    private CountingSemaphore() {
        super();
    }

    private CountingSemaphore(int bound) {
        this();
        this.bound = bound;
    }

    synchronized private void take() throws InterruptedException {
        while (this.signals == bound)
            wait();
        log.info("Take the semaphore: " + Thread.currentThread());
        signals++;
        //This is not for other take, this is for other release
        notify();
    }

    synchronized private void release() throws InterruptedException {
        while (this.signals == 0)
            wait();
        log.info("release the semaphore: " + Thread.currentThread());
        signals--;
        //This is not for other release, this is for other take
        notify();
    }
}