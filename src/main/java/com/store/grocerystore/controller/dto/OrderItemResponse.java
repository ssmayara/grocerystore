package com.store.grocerystore.controller.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
    String productName,
    Integer quantity,
    Integer weightGrams,
    BigDecimal unitPrice,
    BigDecimal discountApplied,
    BigDecimal lineTotal
) {

}
