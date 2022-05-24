package com.magicliang.experiments.aspect.configurable;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * project name: spring-experiments
 * <p>
 * description: 被织入的类
 * <p>
 * 使用 javaagent 要改启动脚本。
 * <p>
 * 要给 jvm 加参数（也就 -javaagent 加在 java 命令后面），而不是 application 加参数（application 的 main class 本身也是 jvm 的一个参数，也就是参数不要加在 -jar abc.jar 后面）：
 * $HOME
 * -javaagent:/Users/magicliang/.m2/repository/org/springframework/spring-instrument/5.2.5.RELEASE/spring-instrument-5.2.5.RELEASE.jar
 * <p>
 * 不要使用这个参数，没用：
 * -Xset:weaveJavaxPackages=true -javaagent:/Users/magicliang/.m2/repository/org/aspectj/aspectjweaver/1.9.5/aspectjweaver-1.9.5.jar
 *
 * @author magicliang
 * <p>
 * date: 2020-04-18 17:43
 */
@Data
// 这个注解不能放在 spring-managed bean 上，不然会导致对象被初始化两次
// 这个注解什么作用都不起，它会指示 AnnotationBeanConfigurerAspect 在 construction前后把依赖注入进这个 bean。注解和切面会联系在一起
// preConstruction 一用上，就会导致注入在 construction 之前。value = "user"，以为着要寻找一个名为 user 的 bean definition
// @Configurable(autowire = Autowire.BY_NAME, dependencyCheck = true)
@Configurable
@Slf4j
public class User {

    @Autowired
    private Dog dog;

    public void output() {
        foo();
    }

    public void foo() {
        log.info("doggy is:" + dog.toString());
    }

    private String name;
    private int age;
}
