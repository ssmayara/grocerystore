package com.store.grocerystore.service;

import com.store.grocerystore.controller.dto.Discount;
import com.store.grocerystore.controller.dto.OrderItemRequest;
import com.store.grocerystore.enums.CategoryType;
import com.store.grocerystore.enums.DiscountType;
import com.store.grocerystore.model.ProductEntity;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BeerDiscountStrategy implements DiscountStrategy {

  private static final int PACK_SIZE = 6;

  @Override
  public boolean supports(CategoryType categoryType) {
    return CategoryType.BEER == categoryType;
  }

  @Override
  public BigDecimal calculate(ProductEntity product,
      OrderItemRequest itemReq,
      BigDecimal baseLineTotal,
      List<Discount> discountsForProduct) {

    int quantity = itemReq.quantity() != null ? itemReq.quantity() : 0;
    if (quantity <= 0) {
      return BigDecimal.ZERO;
    }

    int packs = quantity / PACK_SIZE;
    if (packs == 0) {
      return BigDecimal.ZERO;
    }

    return discountsForProduct.stream()
        .filter(d -> DiscountType.FIXED_AMOUNT.name().equalsIgnoreCase(d.ruleType()))
        .filter(d -> d.packSize() != null && d.packSize() == PACK_SIZE)
        .findFirst()
        .map(d -> {
          BigDecimal perPack = d.discountAmount();
          if (perPack == null) {
            return BigDecimal.ZERO;
          }
          return perPack.multiply(BigDecimal.valueOf(packs));
        })
        .orElse(BigDecimal.ZERO);
  }
}
