package pl.com.tt.ttime.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class JiraRestConfig {
    @Value("${ttimemanager.jira.username}")
    private String username;

    @Value("${ttimemanager.jira.password}")
    private String password;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.basicAuthorization(username, password).build();
    }
}
