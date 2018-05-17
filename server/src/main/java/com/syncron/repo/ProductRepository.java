package com.syncron.repo;

import org.springframework.data.repository.reactive.RxJava2CrudRepository;

public interface ProductRepository extends RxJava2CrudRepository<MongoProduct, Long> {
}
