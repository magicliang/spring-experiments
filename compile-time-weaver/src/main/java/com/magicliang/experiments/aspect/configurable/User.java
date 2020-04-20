package com.magicliang.experiments.aspect.configurable;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * project name: spring-experiments
 * <p>
 * description: 被织入的类
 * <p>
 * 使用 javaagent 要改启动脚本。
 * <p>
 * 要给 jvm 加参数，而不是 application 加参数（application 的 main class 本身也是 jvm 的一个参数）：
 * $HOME
 * -javaagent:${HOME}/.m2/repository/org/springframework/spring-instrument/5.2.5.RELEASE/spring-instrument-5.2.5.RELEASE.jar
 * <p>
 * 不要使用这个参数，没用：
 * -Xset:weaveJavaxPackages=true -javaagent:${HOME}/.m2/repository/org/aspectj/aspectjweaver/1.9.5/aspectjweaver-1.9.5.jar
 *
 * @author magicliang
 * <p>
 * date: 2020-04-18 17:43
 */
// 这个注解不能放在 spring-managed bean 上，不然会导致对象被初始化两次
// 这个注解什么作用都不起，它会指示 AnnotationBeanConfigurerAspect 在 construction 前后把依赖注入进这个 bean。注解和切面会联系在一起
// preConstruction 一用上，就会导致注入在 construction 之前。value = "user"，以为着要寻找一个名为 user 的 bean definition
// @Configurable(autowire = Autowire.BY_NAME, dependencyCheck = true)
@Configurable
public class User {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(User.class);

    @Autowired
    private Dog dog;

    public User() {
    }

    public void output() {
        foo();
    }

    public void foo() {
        log.info("doggy is:" + dog.toString());
    }

    private String name;
    private int age;

    public Dog getDog() {
        return this.dog;
    }

    public String getName() {
        return this.name;
    }

    public int getAge() {
        return this.age;
    }

    public void setDog(Dog dog) {
        this.dog = dog;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof User)) return false;
        final User other = (User) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$dog = this.getDog();
        final Object other$dog = other.getDog();
        if (this$dog == null ? other$dog != null : !this$dog.equals(other$dog)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        if (this.getAge() != other.getAge()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof User;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $dog = this.getDog();
        result = result * PRIME + ($dog == null ? 43 : $dog.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        result = result * PRIME + this.getAge();
        return result;
    }

    public String toString() {
        return "User(dog=" + this.getDog() + ", name=" + this.getName() + ", age=" + this.getAge() + ")";
    }
}
