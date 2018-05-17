package com.syncron;

import com.syncron.mim.MIMMapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Import(ModelConfig.class)
@ComponentScan(basePackageClasses = RxTrainingService.class)
public class ClientConfig {

    public static final String BASE_URL = "https://rxjava-training.herokuapp.com";

    @Bean
    WebClient webClient() {
        return WebClient.create(BASE_URL);
    }
}
