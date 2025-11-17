package com.store.grocerystore.controller.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
    Long orderId,
    BigDecimal total,
    List<OrderItemResponse> items
) {

}