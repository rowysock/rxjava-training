package com.syncron.repo;

import com.syncron.Product;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class MongoProduct extends Product{
}
