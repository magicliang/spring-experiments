package com.magicliang.experiments.entity;

import lombok.extern.slf4j.Slf4j;

/**
 * project name: spring-experiments
 * <p>
 * description: java 程序员
 *
 * @author magicliang
 * <p>
 * date: 2020-04-25 19:14
 */
@Slf4j
public class JavaProgrammer implements Programmer {

    /**
     * 编程语言
     */
    @Override
    public void programLanguage() {
        log.info("Java");
    }
}
