package com.magicliang.experiments.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by magicliang on 2016/6/26.
 */
public class LockTest {
    private static final Logger log = LoggerFactory.getLogger(LockTest.class);
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);
    //Reentrantlock是共有的，Condition也是
    private static final ReentrantLock lock1 = new ReentrantLock();
    private static final Condition condition1 = lock1.newCondition();
    private static volatile int num1 = 0;
    private static volatile int num2 = 0;

    public static void main(String[] args) {
        executor.execute(() -> {
            try {
                lock1.lock();
                Thread.sleep(5000L);
                while (num1 < 1) {
                    log.info("T1 waiting for num1, release lock1， num1： " + num1);
                    condition1.await();
                }
                //注意，条件一满足就应该signal，不要有任何耽搁
                num2++;
                condition1.signal();
                log.info("T1 complete, release lock1， num1: " + num1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock1.unlock();
            }
        });
        executor.execute(() -> {
            try {
                lock1.lock();
                Thread.sleep(1000L);
                //这种互相供应的关系，本线程要取得锁和条件，必须先让其他线程释放锁和满足条件，所以这里的signalAll要提前
                //不然必然导致非死锁的饥饿
                num1++;
                condition1.signalAll();//注意，此处虽然signalAll了，但本身还未释放锁，需要到本线程await的地方，才能让其他线程得到锁，回到await的地方
                while (num2 < 1) {
                    log.info("T2 waiting for num1, release lock1, num1: " + num1);
                    condition1.await();
                }

                log.info("T2 complete, release lock1, num2: " + num2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock1.unlock();
            }
        });
        executor.shutdown();
    }
}