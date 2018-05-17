package com.syncron;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.syncron.repo.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoRepositories(basePackageClasses = ProductRepository.class)
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Bean
    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create("mongodb://test:test@ds227858.mlab.com:27858/syncron-test?waitqueuemultiple=100");
    }

    @Override
    protected String getDatabaseName() {
        return "syncron-test";
    }

}
