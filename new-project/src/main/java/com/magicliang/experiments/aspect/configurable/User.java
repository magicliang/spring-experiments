package com.magicliang.experiments.aspect.configurable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import java.util.Arrays;

/**
 * project name: spring-experiments
 * <p>
 * description: 被织入的类
 *
 * @author magicliang
 * <p>
 * date: 2020-04-18 17:43
 */
@EnableSpringConfigured
@Configurable
public class User {
    @Autowired
    ApplicationContext applicationContext;
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return Arrays.asList(applicationContext.getBeanDefinitionNames()).toString();
    }
}
