package com.syncron;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.mongojack.JacksonDBCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class ServerConfig {

    @Autowired
    ObjectMapper baseMapper;

    @Bean
    ObjectMapper mongoObjectMapper() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateTimeFormatter));
        return baseMapper.copy()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS)
                .registerModule(javaTimeModule);
    }


    @Bean
    MongoClient mongoClient() {
        MongoClientURI uri = new MongoClientURI("mongodb://test:test@ds227858.mlab.com:27858/syncron-test");
        return new MongoClient(uri);
    }

    @Bean
    DB mongoDB() {
        return mongoClient().getDB("syncron-test");
    }

    @Bean
    JacksonDBCollection<Order, String> orders() {
        return JacksonDBCollection.wrap(
                mongoDB().getCollection("orders"),
                Order.class, String.class, mongoObjectMapper());
    }

    @Bean
    JacksonDBCollection<Product, String> products() {
        return JacksonDBCollection.wrap(
                mongoDB().getCollection("products"),
                Product.class, String.class, mongoObjectMapper());
    }

    @Bean
    JacksonDBCollection<OrderDetail, String> orderDetails() {
        return JacksonDBCollection.wrap(
                mongoDB().getCollection("order-details"),
                OrderDetail.class, String.class, mongoObjectMapper());
    }

}
