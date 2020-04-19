package com.magicliang.experiments;

import com.magicliang.experiments.aspect.configurable.Dog;
import com.magicliang.experiments.aspect.configurable.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/res/v1")
@Slf4j
// 只有打开这个注解， @Configurable 注解才会生效
@EnableSpringConfigured
@SpringBootApplication
public class AspectjLoadTimeWeaverApplication {

    public static void main(String[] args) {
        SpringApplication.run(AspectjLoadTimeWeaverApplication.class, args);
    }

    @GetMapping("/user")
    public User getUser() {
        User user = new User();
        user.output();
        return user;
    }

    // 没什么卵用的 ConditionalOnClass
    // @ConditionalOnClass(AnnotationBeanConfigurerAspect.class)
    @Bean
    Dog dog() {
        Dog d = new Dog();
        d.setId(1);
        d.setName("dog");
        return d;
    }

    // 这个 bean 方法有的项目建议有，但其实没有也无所谓
//    @Bean
//    public ProfilingAspect interceptor() {
//        // This will barf at runtime if the weaver isn't working (probably a
//        // good thing)
//        return Aspects.aspectOf(ProfilingAspect.class);
//    }

}
