package com.syncron.repo;

import com.syncron.OrderDetail;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "order-details")
public class MongoOrderDetail extends OrderDetail {
}
