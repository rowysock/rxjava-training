package com.syncron;

import com.syncron.mim.ImmutableMIMItem;
import com.syncron.mim.ImmutableMIMOrder;
import com.syncron.mim.MIMItem;
import com.syncron.mim.MIMOrder;

import java.math.BigDecimal;

class MIMMapper {

    MIMItem mapToItem(Product product) {
        return ImmutableMIMItem.builder()
                .itemCode(String.valueOf(product.productID()))
                .cost(BigDecimal.valueOf(product.unitPrice()))
                .currentStock(product.unitsInStock())
                .build();
    }

    MIMOrder mapToOrder(Order order, OrderDetail detail) {
        return ImmutableMIMOrder.builder()
                .customerCode(order.customerID())
                .itemCode(String.valueOf(detail.productID()))
                .orderDate(order.orderDate())
                .quantity(detail.quantity())
                .build();
    }
}
