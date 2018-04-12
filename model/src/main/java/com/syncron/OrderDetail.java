package com.syncron;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableOrderDetail.class)
@JsonSerialize(as = ImmutableOrderDetail.class)
public interface OrderDetail {

    Long orderID();

    Long productID();

    Long unitPrice();

    Long quantity();

}
