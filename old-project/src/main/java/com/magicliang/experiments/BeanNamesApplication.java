package com.magicliang.experiments;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

/**
 * Created by magicliang on 2016/2/27.
 */
// @SpringBootApplication
public class BeanNamesApplication {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(BeanNamesApplication.class, args);
        System.out.println("Let's inspect the beans provided by Spring Boot:");
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);

        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
    }
}
