package com.store.grocerystore.controller.dto;

import java.util.List;

public record OrderRequest(
    List<OrderItemRequest> items
) {

}