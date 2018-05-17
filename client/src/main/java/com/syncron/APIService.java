package com.syncron;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.adapter.rxjava.RxJava2Adapter;

@Component
public class APIService {

    @Autowired
    WebClient client;

    /**
     * List of all products
     */
    public Observable<Product> getProducts() {
        return client.get().uri("products")
                .retrieve()
                .bodyToFlux(Product.class)
                .as(RxJava2Adapter::fluxToObservable);
    }

    /**
     * Order details for single product. Takes 1 second to respond
     */
    public Observable<OrderDetail> getOrderDetails(long id) {
        return client.get().uri("products/{id}/order-details", id)
                .retrieve()
                .bodyToFlux(OrderDetail.class)
                .as(RxJava2Adapter::fluxToObservable);
    }

    /**
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException with code 404 when order is missing
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException with code 400 when id is malformed
     */
    public Maybe<Order> getOrder(long orderId) throws WebClientResponseException {
        return client.get().uri("orders/{orderId}", orderId)
                .retrieve()
                .bodyToMono(Order.class)
                .as(RxJava2Adapter::monoToMaybe);
    }

    /**
     * Stream of orders
     */
    public Observable<Order> observeOrders() {
        return client.get()
                .uri("order-stream")
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<Order>>() {})
                .filter(sse -> sse.data() != null)
                .map(ServerSentEvent::data)
                .as(RxJava2Adapter::fluxToObservable);
    }

}
