package com.magicliang.experiments.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by magicliang on 2016/6/26.
 */
public class CyclicBarrierTest {
    private static final Logger log = LoggerFactory.getLogger(CyclicBarrierTest.class);
    //必须有足够的线程给出await，少一个都会卡死所有其他await的线程。如果主线程await了，主线程都会卡死
    private static final CyclicBarrier c1 = new CyclicBarrier(2);
    //第二个参数其实是个continuation
    private static final CyclicBarrier c2 = new CyclicBarrier(2, () -> {
        log.info("T5 goes!!");//T5甚至会在T3T4继续执行之前执行，所有跨过篱笆以后顺序是不可以保证的，最好所有的前置线程，都终结了，这样就不会有顺序问题
    });
    //如果这里的线程数不够的话，第一个线程await以后，甚至都不会让第二个线程切换执行，永远都不会await，也就永远锁死了。
    //private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        (new Thread(() -> {
            try {
                log.info("T1 meet the barrier");
                //await既是到达的标志，也是跃跃欲试地快要跨越的地方
                c1.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            log.info("T1 countinue");
        })).start();

        (new Thread(() -> {
            try {
                log.info("T2 meet the barrier");
                c1.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            log.info("T2 countinue");
        })).start();
        /*
        输出是不定的，但最后一个meet barrier的，往往的第一个跨越barrier的，例如：
18:10:34.090 [Thread-0] INFO  com.example.Util.CyclicBarrierTest - T1 meet the barrier
18:10:34.090 [Thread-1] INFO  com.example.Util.CyclicBarrierTest - T2 meet the barrier
18:10:34.095 [Thread-1] INFO  com.example.Util.CyclicBarrierTest - T2 countinue
18:10:34.095 [Thread-0] INFO  com.example.Util.CyclicBarrierTest - T1 countinue

        * */

        executor.execute(() -> {
            try {
                log.info("T3 meet the barrier");
                //这里有一个bug很有意思，如果使用了c1，会使t1，t2继续，t3却不能继续
                //c1是可以被继续使用的，所以是可循环的。
                /*
                * CyclicBarrier和CountDownLatch的区别

CountDownLatch的计数器只能使用一次。而CyclicBarrier的计数器可以使用reset() 方法重置。所以CyclicBarrier能处理更为复杂的业务场景，比如如果计算发生错误，可以重置计数器，并让线程们重新执行一次。
CyclicBarrier还提供其他有用的方法，比如getNumberWaiting方法可以获得CyclicBarrier阻塞的线程数量。isBroken方法用来知道阻塞的线程是否被中断。比如以下代码执行完之后会返回true。
                * */
                c2.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            log.info("T3 countinue");
        });
        executor.execute(() -> {
            try {
                log.info("T4 meet the barrier");
                c2.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            log.info("T4 countinue");
        });
        executor.shutdown();
    }
}
