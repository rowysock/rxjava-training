package com.syncron.repo;

import io.reactivex.Flowable;
import org.springframework.data.repository.reactive.RxJava2CrudRepository;

public interface OrderDetailRepository extends RxJava2CrudRepository<MongoOrderDetail, Long> {

    Flowable<MongoOrderDetail> findByProductID(long productID);

}
