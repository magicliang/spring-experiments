package com.magicliang.experiments.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by magicliang on 2016/6/30.
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }


    @Bean
    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter =
                new MappingJackson2HttpMessageConverter();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);

        return jackson2HttpMessageConverter;
    }
    //必须配上maven的dependency才行。不然即使在ide里看得到，工程里也看不到。
    //明明应该被集成进spring里了才对
//    @Bean
//    public CommonsMultipartResolver multipartResolver() {
//        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//        multipartResolver.setDefaultEncoding(StandardCharsets.UTF_8.name());
//        // 指定所上传文件的总大小不能超过100M
//        // 注意maxUploadSize属性的限制不是针对单个文件, 而是所有文件的容量之和
//        multipartResolver.setMaxUploadSize(104857600);
//        multipartResolver.setMaxInMemorySize(4096);
//
//        return multipartResolver;
//    }

    //Why need this?
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 解决Spring3.1以上版本返回JSON时中文显示乱码问题
        converters.addAll(Lists.newArrayList(stringHttpMessageConverter(),
                jackson2HttpMessageConverter()));
    }
//Don't need this, we have spring security
//    // Support Cross-site HTTP request
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/api/**")
//                .allowedOrigins("*")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS", "PATCH")
//                .allowedHeaders("X-Requested-With", "Content-Type", "Accept", "Authorization")
//                .allowCredentials(true)
//                .maxAge(3600);
//    }
}
