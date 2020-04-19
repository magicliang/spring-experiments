package com.magicliang.experiments;

import com.magicliang.experiments.aspect.configurable.Dog;
import com.magicliang.experiments.aspect.configurable.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.aspectj.AnnotationBeanConfigurerAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/res/v1")
@Slf4j
// 不同的环境有不同的 LoadTimeWeaver 实现
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
@EnableSpringConfigured
@SpringBootApplication
public class SpringExperimentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringExperimentsApplication.class, args);
    }

    @GetMapping("/user")
    public User getUser() {
        User user = new User();
        user.output();
        return user;
    }

    @Bean
    @ConditionalOnClass(AnnotationBeanConfigurerAspect.class)
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
