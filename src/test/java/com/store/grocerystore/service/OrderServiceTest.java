package com.store.grocerystore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.store.grocerystore.controller.dto.OrderItemRequest;
import com.store.grocerystore.controller.dto.OrderItemResponse;
import com.store.grocerystore.controller.dto.OrderRequest;
import com.store.grocerystore.controller.dto.OrderResponse;
import com.store.grocerystore.exceptions.OrderValidationException;
import com.store.grocerystore.model.OrderEntity;
import com.store.grocerystore.model.OrderItemEntity;
import com.store.grocerystore.model.ProductEntity;
import com.store.grocerystore.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private OrderItemService orderItemService;

  @InjectMocks
  private OrderService orderService;

  @Test
  void createOrder_shouldThrowWhenItemsNullOrEmpty() {
    OrderRequest requestWithNullItems = new OrderRequest(null);

    assertThrows(OrderValidationException.class,
        () -> orderService.createOrder(requestWithNullItems));

    OrderRequest requestWithEmptyItems = new OrderRequest(List.of());

    assertThrows(OrderValidationException.class,
        () -> orderService.createOrder(requestWithEmptyItems));
  }

  @Test
  void createOrder_shouldCreateOrderAndCalculateTotal() {
    OrderItemRequest itemReq1 = new OrderItemRequest(1L, 2, null);
    OrderItemRequest itemReq2 = new OrderItemRequest(2L, 1, 500);

    OrderRequest request = new OrderRequest(List.of(itemReq1, itemReq2));

    OrderItemEntity item1 = new OrderItemEntity();
    item1.setQuantity(2);
    item1.setLineTotal(new BigDecimal("10.00"));

    ProductEntity product1 = new ProductEntity();
    product1.setName("Apple");
    item1.setProduct(product1);

    OrderItemEntity item2 = new OrderItemEntity();
    item2.setQuantity(1);
    item2.setLineTotal(new BigDecimal("5.50"));

    ProductEntity product2 = new ProductEntity();
    product2.setName("Carrot");
    item2.setProduct(product2);

    when(orderItemService.createOrderItem(itemReq1)).thenReturn(item1);
    when(orderItemService.createOrderItem(itemReq2)).thenReturn(item2);

    when(orderRepository.save(any(OrderEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    OrderResponse response = orderService.createOrder(request);

    assertNotNull(response);
    assertEquals(new BigDecimal("15.50"), response.total());

    assertEquals(2, response.items().size());

    OrderItemResponse respItem1 = response.items().get(0);
    assertEquals("Apple", respItem1.productName());
    assertEquals(2, respItem1.quantity());
    assertEquals(new BigDecimal("10.00"), respItem1.lineTotal());

    OrderItemResponse respItem2 = response.items().get(1);
    assertEquals("Carrot", respItem2.productName());
    assertEquals(1, respItem2.quantity());
    assertEquals(new BigDecimal("5.50"), respItem2.lineTotal());

    verify(orderItemService, times(1)).createOrderItem(itemReq1);
    verify(orderItemService, times(1)).createOrderItem(itemReq2);
    verify(orderRepository, times(1)).save(any(OrderEntity.class));
  }
}
