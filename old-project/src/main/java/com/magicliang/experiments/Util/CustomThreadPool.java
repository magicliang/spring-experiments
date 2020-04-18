package com.magicliang.experiments.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by magicliang on 2016/6/25.
 */
public class CustomThreadPool {
    private static final Logger log = LoggerFactory.getLogger(CustomThreadPool.class);
    private BlockingQueue<Runnable> taskQueue;
    private List<PooledThread> threads;
    private boolean isStopped;

    public static void main(String[] args) throws InterruptedException {
        CustomThreadPool pool = new CustomThreadPool(2, 2);
        for (int i = 0; i < 2; i++) {
            pool.execute(() -> {
                log.info("current thread is: " + Thread.currentThread());
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        Thread.sleep(20000L);
        pool.stop();//不关闭线程池， JVM 无法关闭
    }

    public CustomThreadPool() {
        super();
    }

    public CustomThreadPool(BlockingQueue<Runnable> taskQueue, List<PooledThread> threads) {
        this();
        this.taskQueue = taskQueue;
        this.threads = threads;
    }

    public CustomThreadPool(int taskCount, int threadCount) {
        /*
        * There are three general strategies for queuing:
Direct handoffs. A good default choice for a work queue is a SynchronousQueue that hands off tasks to threads without otherwise holding them. Here, an attempt to queue a task will fail if no threads are immediately available to run it, so a new thread will be constructed. This policy avoids lockups when handling sets of requests that might have internal dependencies. Direct handoffs generally require unbounded maximumPoolSizes to avoid rejection of new submitted tasks. This in turn admits the possibility of unbounded thread growth when commands continue to arrive on average faster than they can be processed.
Unbounded queues. Using an unbounded queue (for example a LinkedBlockingQueue without a predefined capacity) will cause new tasks to wait in the queue when all corePoolSize threads are busy. Thus, no more than corePoolSize threads will ever be created. (And the value of the maximumPoolSize therefore doesn't have any effect.) This may be appropriate when each task is completely independent of others, so tasks cannot affect each others execution; for example, in a web page server. While this style of queuing can be useful in smoothing out transient bursts of requests, it admits the possibility of unbounded work queue growth when commands continue to arrive on average faster than they can be processed.
Bounded queues. A bounded queue (for example, an ArrayBlockingQueue) helps prevent resource exhaustion when used with finite maximumPoolSizes, but can be more difficult to tune and control. Queue sizes and maximum pool sizes may be traded off for each other: Using large queues and small pools minimizes CPU usage, OS resources, and context-switching overhead, but can lead to artificially low throughput. If tasks frequently block (for example if they are I/O bound), a system may be able to schedule time for more threads than you otherwise allow. Use of small queues generally requires larger pool sizes, which keeps CPUs busier but may encounter unacceptable scheduling overhead, which also decreases throughput.
        * */
        //this.taskQueue = new LinkedBlockingDeque<>(taskCount);
        this.taskQueue = new LinkedBlockingQueue<>(taskCount);
        this.threads = new ArrayList<>(threadCount);
        for (int i = 0; i < threadCount; i++) {
            //threads.add(new PooledThread(taskQueue));
            threads.add(new PooledThread());
        }
        final int[] i = new int[]{0};
        //多线程启动多线程
        threads.stream().parallel().forEach((t) -> {
            t.start();
            t.setName("CustomThreadPool-WorkerThread" + i[0]++);
        });
        log.info("Thread pool started");
    }

    public synchronized void execute(Runnable task) {
        if (this.isStopped) throw
                new IllegalStateException("ThreadPool is stopped");
        //只要处理入队就够
        try {
            taskQueue.put(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //这实际上是shutdownnow，不是shutdown，shutdown会等，而shutdown now会interrupt
    //还需要实现awaitTermination
    public synchronized void stop() {
        isStopped = true;
        threads.forEach((s) -> {
            s.stopThread();
            //即使走到这一步，线程可能还没有结束，要等它退出循环和弹栈后，才算结束
            log.info("This thread is:" + s.getName() + " ,getState:" + s.getState() + ", isAlive:  " + s.isAlive());
            //s.stop();//This method is deprecated and will cause exception in LinkedBlockingDeque and  LinkedBlockingQueue
        });
        log.info("Thread pool stopped");
    }


    private class PooledThread extends Thread {
        private boolean stopped = false;

        //        private  BlockingDeque<Runnable> taskQueue;
//        public PooledThread(BlockingDeque<Runnable> taskQueue){
//            this();
//            this.taskQueue = taskQueue;
//        }
        public PooledThread() {
        }

        synchronized public boolean stopThread() {
            this.stopped = true;
            //在runnable内部找currentthread interrupt，在thread里对thisinterrupt
            this.interrupt();
            return this.stopped;
        }

        public synchronized boolean isStopped() {
            return isStopped;
        }

        @Override
        public void run() {
            //Use this to stop from spin
            //From outside, also need to interrupt the current thread
            while (!stopped) {
                try {
                    //Because here is blocking operation, we have to catch InterruptedException here.
                    //Use closure variable
                    //让线程内部自己通过消息队列通信
                    log.info("thread is running");
                    //It will be blocked
                    Runnable runnable = taskQueue.take();
                    log.info("fetch a task to run");
                    //从这一点上看， Runnable 比 Thread 有一个额外的优势，就是它可以被复用
                    //一个Thread就是一个线程，但它可以切换多个runnable。
                    runnable.run();
                    log.info("task finished");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    //Don't break here, only the stop flag can stop the worker thread. The interrupt will break the waiting.
                }
            }
            log.info("thread is closing");
        }
    }
}
