package com.jokes.provider.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class WebClientConfiguration {

    @Bean
    public HttpClient randomJokesWebClient() {
        return HttpClient.newHttpClient();
    }

}
