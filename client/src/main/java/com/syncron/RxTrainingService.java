package com.syncron;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class RxTrainingService {

    private static final Logger logger = LoggerFactory.getLogger(RxTrainingService.class);

    @Autowired
    APIService apiService;

    @Autowired
    MIMMapper mapper;

    @Autowired
    ReactiveFileWriter fileWriter;

    @Autowired
    SSEListenerFactory sseListenerFactory;

    @Autowired
    ProcessingService processingService;

    /**
     * Should return Observable with non-discontinued products
     * <p>
     * Operators:
     * - filter
     * - flattenAsObservable
     * <p>
     * Utils:
     * - apiService.getProducts()
     */
    Observable<Product> getProducts() {
        return apiService.getProducts()
                .flattenAsObservable(Functions.identity())
                .filter(product -> !product.discontinued());
    }

    /**
     * Should return Observable with order details for single product. Requests should be sent in parallel
     * <p>
     * Operators:
     * - subscribeOn
     * - doOn
     * <p>
     * Utils:
     * - apiService.getOrderDetails
     */
    Observable<OrderDetail> getOrderDetails(Product product) {
        long id = product.productID();
        return apiService.getOrderDetails(id)
                .doOnSuccess(details -> logger.debug("Fetched {} order lines for {}", details.size(), product.productName()))
                .subscribeOn(Schedulers.io())
                .flattenAsObservable(Functions.identity());
    }

    /**
     * Should return single ignoring 404 error
     * <p>
     * Operators:
     * - onError
     * <p>
     * Utils:
     * - apiService.getOrder
     */
    Maybe<Order> getOrder(OrderDetail orderDetail) {
        long id = orderDetail.orderID();
        return apiService.getOrder(id)
                .subscribeOn(Schedulers.io())
                .onErrorComplete();
    }

    /**
     * Should download products and orders to provided files. Should fetch products only once
     * <p>
     * Operators:
     * - map
     * - flatMap
     * - publish
     * - as
     * - merge
     * <p>
     * Utils:
     * - mapper
     * - fileWriter.file
     */
    Completable downloadAll(Path itemsDest, Path ordersDest) {
        Observable<Product> products = getProducts()
                .publish()
                .autoConnect(2);

        Completable downloadItems = products
                .map(mapper::mapToItem)
                .as(fileWriter.file(itemsDest));

        Completable downloadOrders = products
                .flatMap(this::getOrderDetails)
                .flatMapMaybe(orderDetails -> this.getOrder(orderDetails)
                        .map(order -> mapper.mapToOrder(order, orderDetails)))
                .as(fileWriter.file(ordersDest));

        return Completable.mergeArray(downloadItems, downloadOrders);
    }

    Disposable processOrdersLive() {
        return sseListenerFactory.createListener("order-stream", Order.class)
                .buffer(2000, TimeUnit.MILLISECONDS)
                .subscribe(processingService::processOrders);
    }
}
