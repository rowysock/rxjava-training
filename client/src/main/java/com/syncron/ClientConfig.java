package com.syncron;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
@Import(ModelConfig.class)
public class ClientConfig {

    public static final String BASE_URL = "http://localhost:8080";

    @Autowired
    ObjectMapper objectMapper;

    @Bean
    Retrofit retrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
    }

    @Bean
    APIService apiService() {
        return retrofit().create(APIService.class);
    }

    @Bean
    MIMMapper mimMapper() {
        return new MIMMapper();
    }

    @Bean
    RxTrainingService rxTrainingService() {
        return new RxTrainingService();
    }

    @Bean
    ReactiveFileWriter reactiveFileWriter() {
        return new ReactiveFileWriter();
    }

    @Bean
    ProcessingService processingService() {
        return new ProcessingService();
    }

    @Bean
    SSEListenerFactory sseListenerFactory() {
        return new SSEListenerFactory(BASE_URL);
    }
}
