package com.store.grocerystore.service;

import com.store.grocerystore.controller.dto.OrderItemRequest;
import com.store.grocerystore.controller.dto.OrderItemResponse;
import com.store.grocerystore.controller.dto.OrderRequest;
import com.store.grocerystore.controller.dto.OrderResponse;
import com.store.grocerystore.exceptions.OrderValidationException;
import com.store.grocerystore.model.OrderEntity;
import com.store.grocerystore.model.OrderItemEntity;
import com.store.grocerystore.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemService orderItemService;

  public OrderService(OrderRepository orderRepository,
      OrderItemService orderItemService) {
    this.orderRepository = orderRepository;
    this.orderItemService = orderItemService;
  }

  public OrderResponse createOrder(OrderRequest request) {

    if (request.items() == null || request.items().isEmpty()) {
      throw new OrderValidationException("Invalid order");
    }

    OrderEntity order = new OrderEntity();
    BigDecimal total = BigDecimal.ZERO;

    for (OrderItemRequest itemReq : request.items()) {
      OrderItemEntity item = orderItemService.createOrderItem(itemReq);
      total = total.add(item.getLineTotal());
      order.addItem(item);
    }

    order.setTotalAmount(total);

    return toResponse(orderRepository.save(order));
  }


  private OrderResponse toResponse(OrderEntity order) {
    List<OrderItemResponse> itemResponses = order.getItems().stream()
        .map(item -> new OrderItemResponse(
            item.getProduct().getName(),
            item.getQuantity(),
            item.getWeightGrams(),
            item.getUnitPrice(),
            item.getDiscountApplied(),
            item.getLineTotal()
        ))
        .toList();

    return new OrderResponse(
        order.getId(),
        order.getTotalAmount(),
        itemResponses
    );
  }
}

