package com.syncron;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.syncron.repo.OrderDetailRepository;
import com.syncron.repo.OrderRepository;
import com.syncron.repo.ProductRepository;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.SampleOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.adapter.rxjava.RxJava2Adapter;

import java.util.concurrent.TimeUnit;

@RestController
public class ServerController {

    private static final Logger logger = LoggerFactory.getLogger(ServerController.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    private final Observable<Order> orderSubject = Observable.interval(100, TimeUnit.MILLISECONDS)
            .flatMapSingle(time -> sampleOrder())
            .publish()
            .autoConnect()
            .doOnNext(order -> logger.error(order.toString()));

    @RequestMapping("/products")
    public Flowable<? extends Product> products() {
        return productRepository.findAll();
    }

    @RequestMapping(value = "/orders/{id}")
    public Single<ResponseEntity<Order>> orders(@PathVariable long id) {
        if (id < 0) {
            return Single.just(ResponseEntity.badRequest().build());
        } else if (id % 10 == 0) {
            return Single.just(ResponseEntity.notFound().build());
        } else {
            return orderRepository.findByOrderID(id)
                    .map(ResponseEntity::ok);
        }
    }

    @RequestMapping("/products/{id}/order-details")
    public Flowable<? extends OrderDetail> orderDetails(@PathVariable long id) {
        return orderDetailRepository.findByProductID(id).delay(1, TimeUnit.SECONDS);
    }

    private Single<Order> sampleOrder() {
        SampleOperation operation = Aggregation.sample(1);
        Aggregation aggregation = Aggregation.newAggregation(operation);
        return mongoTemplate.aggregate(aggregation, "orders", Order.class)
                .single()
                .as(RxJava2Adapter::monoToSingle);
    }

    @RequestMapping(value = "/order-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Observable<Order> orderStream() throws JsonProcessingException {
        return orderSubject;
    }

}
