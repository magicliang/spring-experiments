package com.magicliang.experiments;

import com.magicliang.experiments.entity.AliEngineer;
import com.magicliang.experiments.entity.Programmer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author magicliang
 */
@RestController
@RequestMapping("/res/v1")
@Slf4j
@SpringBootApplication
public class Introduction {

    /**
     * 自动注入 bean
     */
    @Autowired
    private AliEngineer aliEngineer;

    public static void main(String[] args) {
        SpringApplication.run(Introduction.class, args);
    }

    @GetMapping("/user")
    public AliEngineer getEngineer() {
        return aliEngineer;
    }

    @Bean
    public CommandLineRunner demo() {
        return (args) -> {
            aliEngineer.level();
            ((Programmer) aliEngineer).programLanguage();
        };
    }

}
