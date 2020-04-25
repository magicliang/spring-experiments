package com.magicliang.experiments.entity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * project name: spring-experiments
 * <p>
 * description: 阿里的 p6
 *
 * @author magicliang
 * <p>
 * date: 2020-04-25 19:16
 */
@Component
@Slf4j
public class AliP6 implements AliEngineer {

    /**
     * 职级
     */
    @Override
    public void level() {
        log.info("p6");
    }
}
