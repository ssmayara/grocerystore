package com.store.grocerystore.controller.dto;

public record OrderItemRequest(
    Long productId,
    Integer quantity,
    Integer weightGrams
) {

}

