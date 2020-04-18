package com.magicliang.experiments.config;

import com.magicliang.experiments.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.ws.rs.HttpMethod;
import java.util.ArrayList;
import java.util.List;

//The configuration can be fetched and be profiled
//Without active profile specified, security config will be loaded anyway.
//The Profile is useless for this significant configuration?
@Profile("prod")
//Even in here, the configuration can be detected.
@Configuration
@Order(1)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Can use this to get datasource
        //DataSource dataSource = (DataSource) applicationContext.getBean("dataSource");
        //csrf session can not be reused in different session
        http
                .authorizeRequests()
                .antMatchers("/").permitAll()
                //If the role does not match, will redirect to error page directly
                .antMatchers("/role_assist").hasAuthority("ASSIST")
                //Don't use ROLE prefix here
                .antMatchers(HttpMethod.GET, "/admin").hasRole("ADMIN")
                .antMatchers("/user").access("hasRole('ADMIN') or hasRole('USER') or hasRole('DBA')")//The is another api named regexMatchers.
                .antMatchers("/upload").permitAll()
                //If the password is incorrect, the realm will prompt again
                .anyRequest().fullyAuthenticated()
                //httpBasic will not add Authorization header to the response
                .and().httpBasic().realmName("yulebaike");//不支持中文！

        http.headers().frameOptions().sameOrigin().httpStrictTransportSecurity();
        //默认的logout，一登出就会报type not found 的404错误
        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/abc")//使用这个url，返回的结果都不需要重定向，而是直接调用那个endpoint
                //.logoutSuccessHandler(logoutSuccessHandler)
                .invalidateHttpSession(true)
                //.addLogoutHandler(logoutHandler)
                .deleteCookies("JSESSIONID");
        //http.csrf().disable();

    }

    //We can combine the configuration into one class
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //LDAP authentication
        auth
                .ldapAuthentication()
                //Only space ou is unavailable. I can add fuck ou to the patterns
                .userDnPatterns("uid={0},ou=people", "uid={0},ou=space", "uid={0},ou=otherpeople", "uid={0},ou=fuck")
                //.userDnPatterns("uid={0},ou=space")
                //.groupSearchBase("ou=groups")
                .contextSource().ldif("classpath:test-server.ldif");
        //In memory authentication
        List<SimpleGrantedAuthority> authList = new ArrayList<>();
        //Don't use ROLE prefix here
        authList.add(new SimpleGrantedAuthority("ASSIST"));
        auth.inMemoryAuthentication()
                .withUser("user")
                .password("user")
                //Don't use ROLE prefix here
                .roles("USER")
                .and()
                .withUser("assist")
                .password("assist")
                //Don't use ROLE prefix here
                .authorities(authList)//The authorities conflicts roles, if authorities is activated,roles will take no effect
                .and()
                .withUser("admin")
                .password("admin")
                //Don't use ROLE prefix here
                .roles("USER", "ADMIN")
                .and()
                .withUser("dba")
                .password("dba")
                //Don't use ROLE prefix here
                .roles("DBA");


        //Another approach: http://www.mkyong.com/spring-security/spring-security-form-login-using-database/
        //This approach: http://www.mkyong.com/spring-security/spring-security-hibernate-annotation-example/
        //All we need to do is to load the user from database, to use this service to create UserDetails
        //I guess the encode will encode the password and try to match the encoded password in the database;
        auth.userDetailsService(customUserDetailsService).passwordEncoder(new BCryptPasswordEncoder())
        ;
//How to use the datasource?
//            //DataSource authentication
//        auth
//                .jdbcAuthentication()
//                //Can not bind wire the datasource automatically.
//                //With application.properties, we can autowire a
//                //JdbcTemplate, which can provide datasource
//                .dataSource((DataSource) applicationContext.getBean("dataSource"))
//                .withDefaultSchema()
//                .withUser("abc").password("abc").roles("USER").and()
//                .withUser("adm").password("adm").roles("USER", "ADMIN");

    }

}

//Don't have to use static inner class
//static inner class is for multiple configuration
//@Configuration
//class AuthenticationConfiguration extends
//        GlobalAuthenticationConfigurerAdapter {
//
//    @Autowired
//    private ApplicationContext applicationContext;
//
////        //Autowired does not support static field
////        @Autowired
////        private JdbcTemplate jt;
//
//    @Override
//    public void init(AuthenticationManagerBuilder auth) throws Exception {
//
//        auth
//                .ldapAuthentication()
//                //Only space ou is unavailable. I can add fuck ou to the patterns
//                .userDnPatterns("uid={0},ou=people", "uid={0},ou=space", "uid={0},ou=otherpeople", "uid={0},ou=fuck")
//                //.userDnPatterns("uid={0},ou=space")
//                //.groupSearchBase("ou=groups")
//                .contextSource().ldif("classpath:test-server.ldif");
//        //In memory authentication
//        auth.inMemoryAuthentication()
//                .withUser("user")
//                .password("user")
//                .roles("USER")
//                .and()
//                .withUser("admin")
//                .password("admin")
//                .roles("ADMIN", "USER");
////How to use the datasource?
////            //DataSource authentication
////        auth
////                .jdbcAuthentication()
////                //Can not bind wire the datasource automatically.
////                //With application.properties, we can autowire a
////                //JdbcTemplate, which can provide datasource
////                .dataSource((DataSource) applicationContext.getBean("dataSource"))
////                .withDefaultSchema()
////                .withUser("abc").password("abc").roles("USER").and()
////                .withUser("adm").password("adm").roles("USER", "ADMIN");
//
//    }
//}