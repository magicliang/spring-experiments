package com.magicliang.experiments;

import com.magicliang.experiments.aspect.configurable.Dog;
import com.magicliang.experiments.aspect.configurable.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/res/v1")
@Slf4j
// 不同的环境有不同的 LoadTimeWeaver 实现，目前强依赖于 aop.xml 文件，否则无法激活 weaver register 各种 aspect
@EnableLoadTimeWeaving
// 只有打开这个注解， @Configurable 注解才会生效
@EnableSpringConfigured
@SpringBootApplication
public class SpringInstrumentLoadTimeWeaverApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringInstrumentLoadTimeWeaverApplication.class, args);
    }

    @GetMapping("/user")
    public User getUser() {
        User user = new User();
        user.output();
        return user;
    }

    @Bean
    Dog dog() {
        Dog d = new Dog();
        d.setId(1);
        d.setName("dog");
        return d;
    }

//    @Bean
//    public CommandLineRunner demo() {
//        return (args) -> {
//            final User user = new User();
//            log.info(user.toString());
//        };
//    }

}
