package com.magicliang.experiments;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.net.URL;

/**
 * Created by magicliang on 2016/2/27.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringBootMvcApplication.class)
@WebAppConfiguration
// https://www.baeldung.com/maven-integration-test
// @IntegrationTest({"server.port=0"})
public class HelloControllerIT {
    @Value("${local.server.port}")
    private int port;
    private URL base;
    private RestTemplate template;

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/abc");
        this.template = new RestTemplate();
    }

    @Test
    public void getHello() throws Exception {
        ResponseEntity<String> response = template.getForEntity(base.toString(), String.class);
        Assert.assertThat(response.getBody(), Matchers.equalTo("abc"));
    }
}
