package com.syncron.mim;

import java.time.LocalDate;

public class MIMOrder {

    private final String itemCode;

    private final LocalDate orderDate;

    private final String customerCode;

    private final long quantity;

    public MIMOrder(String itemCode, LocalDate orderDate, String customerCode, long quantity) {
        this.itemCode = itemCode;
        this.orderDate = orderDate;
        this.customerCode = customerCode;
        this.quantity = quantity;
    }

    public String getItemCode() {
        return itemCode;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public long getQuantity() {
        return quantity;
    }
}
