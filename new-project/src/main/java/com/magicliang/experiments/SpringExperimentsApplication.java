package com.magicliang.experiments;

import com.magicliang.experiments.aspect.configurable.Dog;
import com.magicliang.experiments.aspect.configurable.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/res/v1")
@Slf4j
// 不同的环境有不同的 LoadTimeWeaver 实现，目前强依赖于 aop.xml 文件，否则无法激活 weaver register 各种 aspect
// @EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
// @EnableSpringConfigured
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
        // @ConditionalOnClass(AnnotationBeanConfigurerAspect.class)
    Dog dog() {
        Dog d = new Dog();
        d.setId(1);
        d.setName("dog");
        return d;
    }

//    @Bean
//    public ProfilingAspect interceptor() {
//        // This will barf at runtime if the weaver isn't working (probably a
//        // good thing)
//        return Aspects.aspectOf(ProfilingAspect.class);
//    }

//    @Bean
//    public CommandLineRunner demo() {
//        return (args) -> {
//            final User user = new User();
//            log.info(user.toString());
//        };
//    }

}
