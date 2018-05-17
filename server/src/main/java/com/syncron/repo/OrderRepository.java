package com.syncron.repo;

import io.reactivex.Single;
import org.springframework.data.repository.reactive.RxJava2CrudRepository;

public interface OrderRepository extends RxJava2CrudRepository<MongoOrder, Long> {

    Single<MongoOrder> findByOrderID(long orderId);

}
