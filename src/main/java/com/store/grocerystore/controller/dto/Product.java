package com.store.grocerystore.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.store.grocerystore.enums.CategoryType;
import com.store.grocerystore.enums.UnitType;
import java.math.BigDecimal;

@JsonPropertyOrder({"id", "name", "category", "unit_type", "unit_price"})
public record Product(
    Long id,
    String name,
    CategoryType category,
    @JsonProperty("unit_type")
    UnitType uniType,
    @JsonProperty("unit_price")
    BigDecimal unitPrice) {

}
