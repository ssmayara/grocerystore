package com.store.grocerystore.mapper;

import com.store.grocerystore.controller.dto.Discount;
import com.store.grocerystore.model.DiscountEntity;

public class DiscountMapper {

  public static Discount toRecord(DiscountEntity e) {
    if (e == null) {
      return null;
    }

    return new Discount(
        e.getId(),
        e.getProductCategory(),
        e.getRuleType(),
        e.getBuyQty(),
        e.getTakeQty(),
        e.getMinDayAge(),
        e.getMaxDayAge(),
        e.getMinWeightGrams(),
        e.getMaxWeightGrams(),
        e.getDiscountPercent(),
        e.getPackSize(),
        e.getBeerCountry(),
        e.getDiscountAmount(),
        e.getActive(),
        e.getCreatedAt(),
        e.getUpdateAt()
    );
  }

  public static DiscountEntity toEntity(Discount r) {
    if (r == null) {
      return null;
    }

    DiscountEntity e = new DiscountEntity();
    e.setId(r.id());
    e.setProductCategory(r.productCategory());
    e.setRuleType(r.ruleType());
    e.setBuyQty(r.buyQty());
    e.setTakeQty(r.takeQty());
    e.setMinDayAge(r.minDayAge());
    e.setMaxDayAge(r.maxDayAge());
    e.setMinWeightGrams(r.minWeightGrams());
    e.setMaxWeightGrams(r.maxWeightGrams());
    e.setDiscountPercent(r.discountPercent());
    e.setPackSize(r.packSize());
    e.setBeerCountry(r.beerCountry());
    e.setDiscountAmount(r.discountAmount());
    e.setActive(r.active());
    e.setCreatedAt(r.createdAt());
    e.setUpdateAt(r.updateAt());
    return e;
  }
}

