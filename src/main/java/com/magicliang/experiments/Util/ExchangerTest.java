package com.magicliang.experiments.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by magicliang on 2016/6/26.
 */
public class ExchangerTest {
    private static final Logger log = LoggerFactory.getLogger(ExchangerTest.class);

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Exchanger<String> exchanger = new Exchanger<>();
        executor.execute(() -> {
            String a = "123";
            try {
                String b = exchanger.exchange(a);
                log.info("b is: " + b);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        executor.execute(() -> {
            String b = "4655";
            try {
                String a = exchanger.exchange(b);
                log.info("a is: " + a);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

}
