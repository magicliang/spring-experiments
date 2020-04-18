package com.magicliang.experiments.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by magicliang on 2016/4/24.
 */
@RestController
@RequestMapping("/testRequest")
public class TestRequestController {
    private static final Logger log = LoggerFactory.getLogger(TestRequestController.class);

    @RequestMapping("/displayHeader")
    public void displayHeader(@RequestHeader("Accept-Encoding") String encoding) {
        log.info("The is: Accept-Encoding" + encoding);
    }

}
