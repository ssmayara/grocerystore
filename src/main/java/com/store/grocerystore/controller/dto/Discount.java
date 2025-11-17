package com.store.grocerystore.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Discount(
    Long id,
    String productCategory,
    String ruleType,
    Integer buyQty,
    Integer takeQty,
    Integer minDayAge,
    Integer maxDayAge,
    Integer minWeightGrams,
    Integer maxWeightGrams,
    BigDecimal discountPercent,
    Integer packSize,
    String beerCountry,
    BigDecimal discountAmount,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updateAt
) {

}