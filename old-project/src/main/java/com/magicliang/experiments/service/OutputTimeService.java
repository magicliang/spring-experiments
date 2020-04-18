package com.magicliang.experiments.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by magicliang on 2016/6/10.
 */
//This component will be loaded limited by Profile
@Profile("dev")
@Component
public class OutputTimeService {
    private static final Logger log = LoggerFactory.getLogger(OutputTimeService.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("The time is now " + dateFormat.format(new Date()));
    }
}
