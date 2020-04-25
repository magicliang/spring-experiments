package com.magicliang.experiments.aspect;

import com.magicliang.experiments.entity.JavaProgrammer;
import com.magicliang.experiments.entity.Programmer;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareParents;
import org.springframework.stereotype.Component;


/**
 * project name: spring-experiments
 * <p>
 * description: 程序员引入者
 *
 * @author magicliang
 * <p>
 * date: 2020-04-25 19:19
 */
@Component
@Aspect
public class ProgrammerIntroducer {

    /**
     * 目标类型树为：AliEngineer 及其子类
     * 引入接口为 Programmer
     * 引入缺省实现为 JavaProgrammer
     */
    @DeclareParents(value = "com.magicliang.experiments.entity.AliP6",
            // @DeclareParents(value="com.magicliang.experiments.entity.*+",
            defaultImpl = JavaProgrammer.class)
    public static Programmer programmer;
}
