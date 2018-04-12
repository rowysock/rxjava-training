package com.syncron.mim;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.LocalDate;

@Value.Immutable
@JsonSerialize(as = ImmutableMIMOrder.class)
public interface MIMOrder {

    String itemCode();

    LocalDate orderDate();

    String customerCode();

    long quantity();
}
