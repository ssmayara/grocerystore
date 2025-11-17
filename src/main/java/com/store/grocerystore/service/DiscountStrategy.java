package com.store.grocerystore.service;

import com.store.grocerystore.controller.dto.Discount;
import com.store.grocerystore.controller.dto.OrderItemRequest;
import com.store.grocerystore.enums.CategoryType;
import com.store.grocerystore.model.ProductEntity;
import java.math.BigDecimal;
import java.util.List;

public interface DiscountStrategy {

  boolean supports(CategoryType categoryType);

  BigDecimal calculate(ProductEntity product,
      OrderItemRequest itemReq,
      BigDecimal baseLineTotal,
      List<Discount> discountsForProduct);
}
