package com.syncron;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ClientConfig.class)
public class RxTrainingServiceTest {

    @Autowired
    RxTrainingService service;

    @Before
    public void setup() {
        service.processingService = spy(service.processingService);
    }

    @Test
    public void shouldFetchProducts() throws InterruptedException {
        service.getProducts()
                .observeOn(Schedulers.newThread())
                .test()
                .awaitDone(10, TimeUnit.SECONDS)
                .assertValueCount(69);
    }

    @Test
    public void shouldFetchOrderDetails() throws InterruptedException {
        service.getOrderDetails(service.getProducts())
                .observeOn(Schedulers.newThread())
                .test()
                .awaitDone(10, TimeUnit.SECONDS)
                .assertNoTimeout()
                .assertValueCount(1927);
    }

    @Test
    public void shouldFetchOrders() throws InterruptedException {
        // Normal
        service.getOrder(10383)
                .test()
                .awaitDone(10, TimeUnit.SECONDS)
                .assertNoTimeout()
                .assertValue(order -> order.getCustomerID().equals("AROUT"));

        // Missing
        service.getOrder(10380)
                .test()
                .awaitDone(10, TimeUnit.SECONDS)
                .assertNoTimeout()
                .assertNoErrors()
                .assertNoValues();

        // Malformed
        service.getOrder(-6)
                .test()
                .awaitDone(10, TimeUnit.SECONDS)
                .assertNoTimeout()
                .assertError(WebClientResponseException.class)
                .assertError(t -> ((WebClientResponseException)t).getStatusCode() == HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldDownloadAll() throws IOException {
        Path itemsDest = File.createTempFile("items", ".json").toPath();
        Path ordersDest = File.createTempFile("orders", ".json").toPath();
        service.downloadAll(itemsDest, ordersDest).blockingAwait();
        assertThat(itemsDest).exists();
        assertThat(Files.lines(itemsDest)).hasSize(69);

        assertThat(ordersDest).exists();
        assertThat(Files.lines(ordersDest)).hasSize(1740);
    }

    @Test
    public void shouldProcessOrdersLive() throws InterruptedException {
        Disposable disposable = service.processOrdersLive();
        Thread.sleep(10 * 1000 + 500);
        disposable.dispose();

        verify(service.processingService, times(5)).processOrders(any());
    }
}
