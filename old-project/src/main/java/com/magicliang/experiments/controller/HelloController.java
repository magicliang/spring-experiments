package com.magicliang.experiments.controller;

import com.magicliang.experiments.entity.Person;
import com.magicliang.experiments.entity.User;
import com.magicliang.experiments.repository.UserRepository;
import com.magicliang.experiments.repository.impl.AnotherUserRepostitory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.Future;


/*
* Advising controllers with @ControllerAdvice and @RestControllerAdvice

The @ControllerAdvice annotation is a component annotation allowing implementation classes to be auto-detected through classpath scanning. It is automatically enabled when using the MVC namespace or the MVC Java config.

Classes annotated with @ControllerAdvice can contain @ExceptionHandler, @InitBinder, and @ModelAttribute annotated methods, and these methods will apply to @RequestMapping methods across all controller hierarchies as opposed to the controller hierarchy within which they are declared.

@RestControllerAdvice is an alternative where @ExceptionHandler methods assume @ResponseBody semantics by default.

Both @ControllerAdvice and @RestControllerAdvice can target a subset of controllers:

// Target all Controllers annotated with @RestController
@ControllerAdvice(annotations = RestController.class)
public class AnnotationAdvice {}

// Target all Controllers within specific packages
@ControllerAdvice("org.example.controllers")
public class BasePackageAdvice {}

// Target all Controllers assignable to specific classes
@ControllerAdvice(assignableTypes = {ControllerInterface.class, AbstractController.class})
public class AssignableTypesAdvice {}
Check out the @ControllerAdvice documentation for more details.
* */

/**
 * Created by magicliang on 2016/2/25.
 */
@RestController
@RequestMapping("/res/v1")
//@ControllerAdvice//这个一个注解使得内部的配置会应用到所有的controller上，所以我们用一个basiccontroller最好了
public class HelloController {
    private static final Logger log = LoggerFactory.getLogger(HelloController.class);
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    private Person person;
    @Resource
    private UserRepository userRepository;
    @Autowired
    private AnotherUserRepostitory anotherUserRepostitory;

    public HelloController() {

    }

    @Autowired//This annotation can be used to most types, better than resource and inject
    public HelloController(Person person) {
        this.person = person;
        log.info("The person's name is: " + person.getName());
    }

    //从文档上看，就是把命令行参数和请求参数绑定到方法参数时的加工器
    //感觉就是个controller里的converter
    //见这里 http://www.bkjia.com/Javabc/1101887.html
    //和这里 http://www.cnblogs.com/HD/p/4123686.html
    //这个注解要配合@ControllerAdvice一起用才好，见上面的注解
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    //Since the error inspection see errors here, comment it. It can work anyway
//    @Autowired
//    private void injectOtherPersons(Person p1, Person p2, Map<String, Person> pc, ApplicationContext applicationContext){
//
//        log.info("1st person's name is:  " + p1.getName());
//        log.info("2nd person's name is:  " + p2.getName());
//        log.info("person collection is:  " + pc.size());
//        log.info("applicationContext  is:  " + applicationContext);
//    }

    @Bean
//A bean factory method
    Person getPerson() {
        return this.person;
    }

    @RequestMapping("/abc")
    public String abc() {
        return "abc你好";
    }

//    @RequestMapping("/")
//    public String index() {
//        return "index";
//    }


    @RequestMapping("logout")
    public String logout() {
        return "logout1";
    }

    @CrossOrigin(origins = "http://localhost:9000")
    @RequestMapping("/efg")
    public String efg() {
        return "efg";
    }

    @Transactional
    @RequestMapping(path = "/user/{name}"
            //,headers = {"Content-type=application/json"}
    )
    public ResponseEntity<User> getUser(@PathVariable String name) throws Exception {
        //Another way to get the request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        log.info("Principal is:  " + request.getUserPrincipal().getName());

        List<User> users = userRepository.findByName(name);
        users = userRepository.findByName1(name);
        Future<List<User>> asyncUsers = anotherUserRepostitory.findByName(name);
        while (!asyncUsers.isDone()) {
            Thread.sleep(10); //10-millisecond pause between each check
        }
        users = asyncUsers.get();
        User user = null;
        if (!users.isEmpty()) {
            user = users.get(0);
        }
        log.info("User is:  " + user);
        CsrfToken token = generateCsrfToken(request);
        //Use this style, to add the real csrf token to the response header
        //Even spring mvc has a csrf protection, it will not add the csrf field in a custom response.
        //The token will be kept in a session, if the server is restarted, the token is expired.
        return ResponseEntity.ok().header("X-CSRF-TOKEN", token.getToken()).body(user);
        //return ResponseEntity.ok().body(user);

    }

    @RequestMapping(path = "/user", method = RequestMethod.POST
            , headers = {"Content-type=application/json"}//这个头可以说是必须的，不然json格式也无法序列化为对象。猜也猜不出。
    )
    //678 在加载后重新开一个窗体是可以用的管理员账号，但用旧的csrf token 就会出问题？
    public ResponseEntity<User> addUser(@Valid//The valid annotation will not make new added user can not be read!
                                        @RequestBody User user, BindingResult result
            , HttpServletRequest request, HttpServletResponse response
    ) throws Exception {
        log.info("Auth type is:  " + request.getAuthType());
        log.info("Principal is:  " + request.getUserPrincipal().getName());
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        CsrfToken token = generateCsrfToken(request);
        return ResponseEntity.ok(userRepository.save(user));
    }

    @RequestMapping(value = "/csrf-token", method = RequestMethod.GET)
    public @ResponseBody
    String getCsrfToken(HttpServletRequest request) {
        CsrfToken token = generateCsrfToken(request);
        return token.getToken();
    }

    @RequestMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }

    private CsrfToken generateCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    }


    //Authorized endpoint

    @RequestMapping("/role_assist")
    public String role_assist() {
        return "role_assist";
    }


    @RequestMapping("/admin")
    public String admin() {
        return "admin";
    }


    @RequestMapping("/user")
    public String user() {
        return "user";
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST
    )
    public String logout(HttpServletRequest request) throws Exception {
        return "logout2";
    }
}
