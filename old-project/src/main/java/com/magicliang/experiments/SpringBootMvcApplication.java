package com.magicliang.experiments;


import com.magicliang.experiments.repository.UserRepository;
import com.magicliang.experiments.service.MultiThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.io.File;

//@RestController
//@EnableAutoConfiguration

//@Transactional
//@SpringBootApplication
//@EnableCaching
//@EnableScheduling
//@Order(-1)
//@Import(WebConfig.class)//don't have to use this, the configuration annotaion will be autowired
//@EnableAspectJAutoProxy//With @SpringBootApplicationm, we do not need this, only need @Aspect and @Component
//With this, we can specify properties not in resources folder
//@PropertySource(value = { "classpath:application.properties" })//Also don't need this
public class SpringBootMvcApplication {

    //    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Logger log = LoggerFactory.getLogger(SpringBootMvcApplication.class);
    public static String ROOT = "upload-dir";
    @Autowired
    MultiThreadService multiThreadService;
    @Autowired
    private Environment env;

    //@RequestMapping("/")
    //String home(){
    //return "Hello, World";
    //}

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMvcApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(UserRepository userRepository) {
        return (args) -> {
            new File(ROOT).mkdir();

            userRepository.deleteInBatch(userRepository.findAll());
            //userRepository.findByName()
            //password: Encrypted 123 $2a$10$W7fimbsfYVHG.nS5ZhqtfeyIftVRfNGeVpsj2RvW.1B8JgKHeClDO
//            userRepository.save(new User("Chuan", passwordEncoder.encode("123")));
//            userRepository.save(new User("Liang", passwordEncoder.encode("123")));
//            userRepository.save(new User("Test", passwordEncoder.encode("123")));
            // fetch all customers
            log.info("Users found with findAll():");
            log.info("-------------------------------");
            userRepository.findAll().stream().parallel().forEach((user) -> log.info(user.toString()));
        };
    }
//In the spring example, we don't need this
// Spring Boot automatically configures a suitable CacheManager to serve as a provider for the relevant cache. See the Spring Boot documentation for more details.
//	@Bean
//	public CacheManager cacheManager()
//	{
//		return new ConcurrentMapCacheManager("users");
//	}

}
