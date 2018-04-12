package com.syncron;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value.Immutable
@JsonDeserialize(as = ImmutableOrder.class)
@JsonSerialize(as = ImmutableOrder.class)
public interface Order {

    Long orderID();

    String customerID();

    String shipName();

    LocalDate orderDate();

    BigDecimal freight();
}
