package com.syncron.repo;

import com.syncron.Order;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "orders")
public class MongoOrder extends Order {
}
