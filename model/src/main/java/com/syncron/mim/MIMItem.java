package com.syncron.mim;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
@JsonSerialize(as = ImmutableMIMItem.class)
public interface MIMItem {

    String itemCode();

    BigDecimal cost();

    Long currentStock();

}
