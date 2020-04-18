package com.magicliang.experiments.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by magicliang on 2016/2/27.
 */
@Component
@Scope("prototype")
public class Person {

    @Value("${personName}")//This sensible variable
    private String name = "Liang";


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
