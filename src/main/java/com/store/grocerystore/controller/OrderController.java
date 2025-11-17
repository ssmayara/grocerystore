package com.store.grocerystore.controller;

import com.store.grocerystore.controller.dto.OrderRequest;
import com.store.grocerystore.controller.dto.OrderResponse;
import com.store.grocerystore.service.OrderService;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;

  }

  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
    OrderResponse response = orderService.createOrder(request);
    return ResponseEntity
        .created(URI.create("/api/orders/" + response.orderId()))
        .body(response);
  }

}
