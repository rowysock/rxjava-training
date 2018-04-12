package com.syncron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.UUID;

public class ProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessingService.class);

    public synchronized void processOrders(Collection<Order> orders) {
        String id = UUID.randomUUID().toString();
        logger.info("Starting processing {}", id);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("Processing {} finished", id);

    }
}
