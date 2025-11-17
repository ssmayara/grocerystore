package com.store.grocerystore.service;

import com.store.grocerystore.controller.dto.Discount;
import com.store.grocerystore.controller.dto.OrderItemRequest;
import com.store.grocerystore.enums.CategoryType;
import com.store.grocerystore.exceptions.MissingWeightException;
import com.store.grocerystore.exceptions.ProductPriceNotConfiguredException;
import com.store.grocerystore.model.OrderItemEntity;
import com.store.grocerystore.model.ProductEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {

  private final ProductService productService;
  private final ProductDiscountService productDiscountService;
  private final List<DiscountStrategy> discountStrategies;

  public OrderItemService(ProductService productService,
      ProductDiscountService productDiscountService,
      List<DiscountStrategy> discountStrategies) {
    this.productService = productService;
    this.productDiscountService = productDiscountService;
    this.discountStrategies = discountStrategies;
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

    List<Discount> discountsForProduct =
        productDiscountService.findDiscountsForProduct(productEntity.getId());

    BigDecimal discountAmount =
        calculateDiscount(productEntity, orderItemRequest, baseLineTotal, discountsForProduct);

    item.setDiscountApplied(discountAmount);
    item.setLineTotal(baseLineTotal.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP));

    return item;
  }

  private BigDecimal calculateDiscount(ProductEntity product,
      OrderItemRequest itemReq,
      BigDecimal baseLineTotal,
      List<Discount> discountsForProduct) {

    if (discountsForProduct == null || discountsForProduct.isEmpty()) {
      return BigDecimal.ZERO;
    }

    String category = product.getCategory();
    if (category == null) {
      return BigDecimal.ZERO;
    }

    CategoryType categoryType;
    try {
      categoryType = CategoryType.valueOf(category.toUpperCase());
    } catch (IllegalArgumentException ex) {
      return BigDecimal.ZERO;
    }

    return discountStrategies.stream()
        .filter(strategy -> strategy.supports(categoryType))
        .findFirst()
        .map(strategy -> strategy.calculate(product, itemReq, baseLineTotal, discountsForProduct))
        .orElse(BigDecimal.ZERO);
  }

  private BigDecimal calculatePriceVegetable(BigDecimal pricePer100g, Integer gram) {
    return pricePer100g
        .multiply(BigDecimal.valueOf(gram))
        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
  }
}
