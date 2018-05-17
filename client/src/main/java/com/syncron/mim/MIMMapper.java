package com.syncron.mim;

import com.syncron.Order;
import com.syncron.OrderDetail;
import com.syncron.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MIMMapper {

    public MIMItem mapToItem(Product product) {
        return new MIMItem(
                String.valueOf(product.getProductID()),
                BigDecimal.valueOf(product.getUnitPrice()),
                product.getUnitsInStock()
        );
    }

    public MIMOrder mapToOrder(Order order, OrderDetail detail) {
        return new MIMOrder(
                order.getCustomerID(),
                order.getOrderDate(),
                String.valueOf(detail.getProductID()),
                detail.getQuantity()
        );
    }
}
