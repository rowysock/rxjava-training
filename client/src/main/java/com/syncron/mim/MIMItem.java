package com.syncron.mim;

import java.math.BigDecimal;

public class MIMItem {

    private final String itemCode;

    private final BigDecimal cost;

    private final Long currentStock;

    public MIMItem(String itemCode, BigDecimal cost, Long currentStock) {
        this.itemCode = itemCode;
        this.cost = cost;
        this.currentStock = currentStock;
    }

    public String getItemCode() {
        return itemCode;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public Long getCurrentStock() {
        return currentStock;
    }
}
