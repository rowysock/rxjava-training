package com.syncron;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Order {

    private long orderID;
    private String customerID;
    private String shipName;
    private LocalDate orderDate;
    private BigDecimal freight;

    public long getOrderID() {
        return orderID;
    }

    public void setOrderID(long orderID) {
        this.orderID = orderID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", customerID='" + customerID + '\'' +
                ", shipName='" + shipName + '\'' +
                ", orderDate=" + orderDate +
                ", freight=" + freight +
                '}';
    }
}
