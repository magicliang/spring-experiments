package com.magicliang.experiments;

import com.magicliang.experiments.aspect.configurable.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class SpringExperimentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringExperimentsApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo() {
        return (args) -> {
            final User user = new User();
            log.info(user.toString());
        };
    }

}
