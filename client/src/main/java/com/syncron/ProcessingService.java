package com.syncron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
public class ProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessingService.class);

    /**
     * Stub processing, which takes one second to complete
     */
    public synchronized void processOrders(Collection<Order> orders) {
        String id = UUID.randomUUID().toString();
        logger.info("Starting processing {} of {} orders", id, orders.size());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("Processing {} finished", id);

    }
}
