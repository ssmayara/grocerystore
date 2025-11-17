package com.store.grocerystore.service;

import com.store.grocerystore.controller.dto.Discount;
import com.store.grocerystore.controller.dto.OrderItemRequest;
import com.store.grocerystore.enums.CategoryType;
import com.store.grocerystore.enums.DiscountType;
import com.store.grocerystore.exceptions.MissingWeightException;
import com.store.grocerystore.exceptions.ProductPriceNotConfiguredException;
import com.store.grocerystore.model.OrderItemEntity;
import com.store.grocerystore.model.ProductEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {

  private final ProductService productService;
  private final ProductDiscountService productDiscountService;

  public OrderItemService(ProductService productService,
      ProductDiscountService productDiscountService) {
    this.productService = productService;
    this.productDiscountService = productDiscountService;
  }

  public OrderItemEntity createOrderItem(OrderItemRequest orderItemRequest) {

    ProductEntity productEntity = productService.findEntityById(orderItemRequest.productId());

    OrderItemEntity item = new OrderItemEntity();
    item.setProduct(productEntity);

    int quantity = orderItemRequest.quantity() != null ? orderItemRequest.quantity() : 1;
    item.setQuantity(quantity);
    item.setWeightGrams(orderItemRequest.weightGrams());

    BigDecimal unitPrice = productEntity.getUnitPrice();
    if (unitPrice == null) {
      throw new ProductPriceNotConfiguredException(productEntity.getId());
    }
    item.setUnitPrice(unitPrice);

    BigDecimal baseLineTotal;
    if (CategoryType.VEGETABLE.name().equalsIgnoreCase(productEntity.getCategory())) {
      Integer grams = orderItemRequest.weightGrams();
      if (grams == null) {
        throw new MissingWeightException();
      }
      baseLineTotal = calculatePriceVegetable(unitPrice, grams);
    } else {
      baseLineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    BigDecimal discountAmount = calculateDiscount(productEntity, orderItemRequest, baseLineTotal);
    item.setDiscountApplied(discountAmount);
    item.setLineTotal(baseLineTotal.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP));

    return item;
  }

  private BigDecimal calculateDiscount(ProductEntity product, OrderItemRequest itemReq,
      BigDecimal baseLineTotal) {

    String category = product.getCategory();
    if (category == null) {
      return BigDecimal.ZERO;
    }

    List<Discount> discountsForProduct = productDiscountService.findDiscountsForProduct(
        product.getId());
    if (discountsForProduct.isEmpty()) {
      return BigDecimal.ZERO;
    }

    if (CategoryType.VEGETABLE.name().equalsIgnoreCase(category)) {
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

    if (CategoryType.BREAD.name().equalsIgnoreCase(category)) {

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

    if (CategoryType.BEER.name().equalsIgnoreCase(category)) {
      int quantity = itemReq.quantity() != null ? itemReq.quantity() : 0;
      if (quantity <= 0) {
        return BigDecimal.ZERO;
      }

      int packSize = 6;
      int packs = quantity / packSize;
      if (packs == 0) {
        return BigDecimal.ZERO;
      }

      return discountsForProduct.stream()
          .filter(d -> DiscountType.FIXED_AMOUNT.name().equalsIgnoreCase(d.ruleType()))
          .filter(d -> d.packSize() != null && d.packSize() == packSize)
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

    return BigDecimal.ZERO;
  }

  private BigDecimal calculatePriceVegetable(BigDecimal pricePer100g, Integer gram) {
    return pricePer100g
        .multiply(BigDecimal.valueOf(gram))
        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
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
