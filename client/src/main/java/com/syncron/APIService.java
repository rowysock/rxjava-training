package com.syncron;

import io.reactivex.Maybe;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

import java.util.List;

public interface APIService {

    /**
     * List of all products
     */
    @GET("/products")
    Single<List<Product>> getProducts();

    /**
     * Order details for single product. Takes 1 second to respond
     */
    @GET("/products/{id}/order-details")
    Single<List<OrderDetail>> getOrderDetails(@Path("id") long id);

    /**
     * Single order. Throws {@link retrofit2.HttpException} with code 404
     */
    @GET("/orders/{id}")
    Maybe<Order> getOrder(@Path("id") long id);

    /**
     * Stream of orders
     */
    @GET("/order-stream")
    @Streaming
    ResponseBody observeOrders();

}
