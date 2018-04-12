package com.syncron;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.mongojack.Aggregation;
import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class ServerController {

    private static final Logger logger = LoggerFactory.getLogger(ServerController.class);

    @Autowired
    JacksonDBCollection<Order, String> orders;
    private final Observable<Order> orderSubject = Observable.interval(100, TimeUnit.MILLISECONDS)
            .map(time -> sampleOrder())
            .publish()
            .autoConnect();
    @Autowired
    JacksonDBCollection<Product, String> products;
    @Autowired
    JacksonDBCollection<OrderDetail, String> orderDetails;

    private Order sampleOrder() {
        DBObject sample = new BasicDBObject("$sample", new BasicDBObject("size", 1));
        return orders.aggregate(new Aggregation<Order>(Order.class, sample)).results().get(0);
    }

    @RequestMapping("/orders/{id}")
    public ResponseEntity<Order> order(@PathVariable Long id) throws JsonProcessingException {
        if (id % 10 == 0) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(orders.findOne(DBQuery.is("OrderID", id)));
        }
    }

    @RequestMapping("/products")
    public List<Product> products() throws JsonProcessingException {
        return products.find().toArray();
    }

    @RequestMapping("/products/{id}")
    public Product product(@PathVariable Long id) throws JsonProcessingException {
        return products.findOne(DBQuery.is("ProductID", id));
    }

    @RequestMapping(value = "/products/{id}/order-details")
    public Single<List<OrderDetail>> productOrderDetails(@PathVariable Long id) throws JsonProcessingException {
        return Single.timer(1, TimeUnit.SECONDS)
                .map(i -> orderDetails.find(DBQuery.is("ProductID", id)).toArray());
    }

    @RequestMapping(value = "/order-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Observable<Order> orderStream() throws JsonProcessingException {
        return orderSubject;
    }

}
