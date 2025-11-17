package com.store.grocerystore.service;

import com.store.grocerystore.controller.dto.Discount;
import com.store.grocerystore.controller.dto.OrderItemRequest;
import com.store.grocerystore.enums.CategoryType;
import com.store.grocerystore.enums.DiscountType;
import com.store.grocerystore.model.ProductEntity;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BreadDiscountStrategy implements DiscountStrategy {

  @Override
  public boolean supports(CategoryType categoryType) {
    return CategoryType.BREAD == categoryType;
  }

  @Override
  public BigDecimal calculate(ProductEntity product,
      OrderItemRequest itemReq,
      BigDecimal baseLineTotal,
      List<Discount> discountsForProduct) {

    int daysOld = calculateBreadAgeInDays(product);
    if (daysOld > 6) {
      return BigDecimal.ZERO;
    }

    int quantity = itemReq.quantity() != null ? itemReq.quantity() : 0;
    if (quantity <= 0) {
      return BigDecimal.ZERO;
    }

    BigDecimal unitPrice = product.getUnitPrice();
    if (unitPrice == null) {
      return BigDecimal.ZERO;
    }

    return discountsForProduct.stream()
        .filter(d -> DiscountType.BUY_TAKE.name().equalsIgnoreCase(d.ruleType()))
        .filter(d -> d.minDayAge() != null && d.maxDayAge() != null)
        .filter(d -> daysOld >= d.minDayAge() && daysOld <= d.maxDayAge())
        .findFirst()
        .map(d -> {
          Integer buyQty = d.buyQty();
          Integer takeQty = d.takeQty();

          if (buyQty == null || takeQty == null || takeQty == 0) {
            return BigDecimal.ZERO;
          }

          int packs = quantity / takeQty;
          if (packs <= 0) {
            return BigDecimal.ZERO;
          }

          int freePerPack = takeQty - buyQty;
          int totalFreeItems = packs * freePerPack;

          return unitPrice.multiply(BigDecimal.valueOf(totalFreeItems));
        })
        .orElse(BigDecimal.ZERO);
  }

  private int calculateBreadAgeInDays(ProductEntity product) {
    LocalDateTime createdAt = product.getCreatedAt();
    if (createdAt == null) {
      return 0;
    }
    long days = Duration.between(createdAt, LocalDateTime.now()).toDays();
    return (int) days;
  }
}
