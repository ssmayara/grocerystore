package com.store.grocerystore.service;

import com.store.grocerystore.controller.dto.Discount;
import com.store.grocerystore.controller.dto.OrderItemRequest;
import com.store.grocerystore.enums.CategoryType;
import com.store.grocerystore.enums.DiscountType;
import com.store.grocerystore.model.ProductEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class VegetableDiscountStrategy implements DiscountStrategy {

  @Override
  public boolean supports(CategoryType categoryType) {
    return CategoryType.VEGETABLE == categoryType;
  }

  @Override
  public BigDecimal calculate(ProductEntity product,
      OrderItemRequest itemReq,
      BigDecimal baseLineTotal,
      List<Discount> discountsForProduct) {

    int weight = itemReq.weightGrams() != null ? itemReq.weightGrams() : 0;

    return discountsForProduct.stream()
        .filter(d -> DiscountType.PERCENTAGE.name().equalsIgnoreCase(d.ruleType()))
        .filter(d -> d.minWeightGrams() != null && d.maxWeightGrams() != null)
        .filter(d -> weight >= d.minWeightGrams() && weight <= d.maxWeightGrams())
        .findFirst()
        .map(d -> {
          BigDecimal percent = d.discountPercent();
          if (percent == null) {
            return BigDecimal.ZERO;
          }
          BigDecimal rate = percent
              .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
          return baseLineTotal.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        })
        .orElse(BigDecimal.ZERO);
  }
}
