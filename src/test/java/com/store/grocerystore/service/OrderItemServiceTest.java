package com.store.grocerystore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

  @Mock
  private ProductService productService;

  @Mock
  private ProductDiscountService productDiscountService;

  private OrderItemService orderItemService;

  public static final Long PRODUCT_ID = 1L;
  public static final Integer WEIGHT_GRANS = 100;
  public static final Integer MIN_WEIGHT_GRANS = 100;
  public static final Integer MAX_WEIGHT_GRANS = 500;
  public static final Integer DEFAULT_QUANTITY = 1;

  @BeforeEach
  void setUp() {
    List<DiscountStrategy> strategies = List.of(
        new VegetableDiscountStrategy(),
        new BreadDiscountStrategy(),
        new BeerDiscountStrategy()
    );

    this.orderItemService = new OrderItemService(
        productService,
        productDiscountService,
        strategies
    );
  }

  @Test
  void createOrderItem_shouldThrowWhenUnitPriceIsNull() {

    ProductEntity product = new ProductEntity();
    product.setId(PRODUCT_ID);
    product.setCategory(CategoryType.VEGETABLE.name());
    product.setUnitPrice(null);

    when(productService.findEntityById(PRODUCT_ID)).thenReturn(product);

    OrderItemRequest request = newOrderItemRequest(PRODUCT_ID, DEFAULT_QUANTITY, WEIGHT_GRANS);

    assertThrows(ProductPriceNotConfiguredException.class,
        () -> orderItemService.createOrderItem(request));

    verify(productService).findEntityById(PRODUCT_ID);
    verifyNoInteractions(productDiscountService);
  }

  @Test
  void createOrderItem_shouldThrowMissingWeightForVegetableWithoutGrams() {

    ProductEntity product = new ProductEntity();
    product.setId(PRODUCT_ID);
    product.setCategory(CategoryType.VEGETABLE.name());
    product.setUnitPrice(new BigDecimal("2.00"));

    when(productService.findEntityById(PRODUCT_ID)).thenReturn(product);

    OrderItemRequest request = newOrderItemRequest(PRODUCT_ID, DEFAULT_QUANTITY, null);

    assertThrows(MissingWeightException.class,
        () -> orderItemService.createOrderItem(request));

    verify(productService).findEntityById(PRODUCT_ID);
    verifyNoInteractions(productDiscountService);
  }

  @Test
  void createOrderItem_shouldCalculateVegetablePriceAndPercentageDiscount() {

    ProductEntity product = new ProductEntity();
    product.setId(PRODUCT_ID);
    product.setCategory(CategoryType.VEGETABLE.name());
    product.setUnitPrice(new BigDecimal("2.00"));

    when(productService.findEntityById(PRODUCT_ID)).thenReturn(product);

    OrderItemRequest request = newOrderItemRequest(PRODUCT_ID, null, WEIGHT_GRANS);

    Discount discount = newPercentageDiscountVegetable(
        new BigDecimal("10"),
        MIN_WEIGHT_GRANS,
        MAX_WEIGHT_GRANS
    );

    when(productDiscountService.findDiscountsForProduct(PRODUCT_ID))
        .thenReturn(List.of(discount));

    OrderItemEntity item = orderItemService.createOrderItem(request);

    BigDecimal expectedBaseLine = new BigDecimal("2.00")
        .multiply(BigDecimal.valueOf(WEIGHT_GRANS))
        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

    BigDecimal expectedDiscount = expectedBaseLine
        .multiply(new BigDecimal("0.10"))
        .setScale(2, RoundingMode.HALF_UP);

    BigDecimal expectedLineTotal = expectedBaseLine
        .subtract(expectedDiscount)
        .setScale(2, RoundingMode.HALF_UP);

    assertEquals(expectedDiscount, item.getDiscountApplied());
    assertEquals(expectedLineTotal, item.getLineTotal());
    assertEquals(DEFAULT_QUANTITY, item.getQuantity());
    assertEquals(WEIGHT_GRANS, item.getWeightGrams());

    verify(productService).findEntityById(PRODUCT_ID);
    verify(productDiscountService).findDiscountsForProduct(PRODUCT_ID);
  }

  @Test
  void createOrderItem_shouldApplyBuyTakeDiscountForBread() {

    ProductEntity product = new ProductEntity();
    product.setId(PRODUCT_ID);
    product.setCategory(CategoryType.BREAD.name());
    product.setUnitPrice(new BigDecimal("3.00"));
    product.setCreatedAt(LocalDateTime.now().minusDays(2));

    when(productService.findEntityById(PRODUCT_ID)).thenReturn(product);

    OrderItemRequest request = newOrderItemRequest(PRODUCT_ID, 4, null);

    Discount discount = newBuyTakeDiscountBread(
        0,
        3,
        3,
        4
    );

    when(productDiscountService.findDiscountsForProduct(PRODUCT_ID))
        .thenReturn(List.of(discount));

    OrderItemEntity item = orderItemService.createOrderItem(request);

    BigDecimal expectedBaseLine = new BigDecimal("3.00")
        .multiply(BigDecimal.valueOf(4));

    BigDecimal expectedDiscount = new BigDecimal("3.00");
    BigDecimal expectedLineTotal = expectedBaseLine.subtract(expectedDiscount);

    assertEquals(expectedDiscount, item.getDiscountApplied());
    assertEquals(expectedLineTotal, item.getLineTotal());
    assertEquals(4, item.getQuantity());

    verify(productService).findEntityById(PRODUCT_ID);
    verify(productDiscountService).findDiscountsForProduct(PRODUCT_ID);
  }

  @Test
  void createOrderItem_shouldApplyFixedAmountDiscountForBeer() {

    ProductEntity product = new ProductEntity();
    product.setId(PRODUCT_ID);
    product.setCategory(CategoryType.BEER.name());
    product.setUnitPrice(new BigDecimal("5.00"));

    when(productService.findEntityById(PRODUCT_ID)).thenReturn(product);

    OrderItemRequest request = newOrderItemRequest(PRODUCT_ID, 12, null);

    Discount discount = newFixedAmountDiscountBeer(
        6,
        new BigDecimal("2.00")
    );

    when(productDiscountService.findDiscountsForProduct(PRODUCT_ID))
        .thenReturn(List.of(discount));

    OrderItemEntity item = orderItemService.createOrderItem(request);

    BigDecimal expectedBaseLine = new BigDecimal("5.00")
        .multiply(BigDecimal.valueOf(12));

    BigDecimal expectedDiscount = new BigDecimal("2.00")
        .multiply(BigDecimal.valueOf(2));

    BigDecimal expectedLineTotal = expectedBaseLine
        .subtract(expectedDiscount)
        .setScale(2, RoundingMode.HALF_UP);

    assertEquals(expectedDiscount, item.getDiscountApplied());
    assertEquals(expectedLineTotal, item.getLineTotal());
    assertEquals(12, item.getQuantity());

    verify(productService).findEntityById(PRODUCT_ID);
    verify(productDiscountService).findDiscountsForProduct(PRODUCT_ID);
  }

  @Test
  void createOrderItem_shouldReturnZeroDiscountWhenNoDiscountsFound() {

    ProductEntity product = new ProductEntity();
    product.setId(PRODUCT_ID);
    product.setCategory(CategoryType.BEER.name());
    product.setUnitPrice(new BigDecimal("4.00"));

    when(productService.findEntityById(PRODUCT_ID)).thenReturn(product);
    when(productDiscountService.findDiscountsForProduct(PRODUCT_ID))
        .thenReturn(List.of());

    OrderItemRequest request = newOrderItemRequest(PRODUCT_ID, 6, null);

    OrderItemEntity item = orderItemService.createOrderItem(request);

    BigDecimal expectedBaseLine = new BigDecimal("4.00")
        .multiply(BigDecimal.valueOf(6));

    assertEquals(BigDecimal.ZERO, item.getDiscountApplied());
    assertEquals(
        expectedBaseLine.setScale(2, RoundingMode.HALF_UP),
        item.getLineTotal()
    );
  }

  private Discount newPercentageDiscountVegetable(
      BigDecimal percent,
      Integer minWeightGrams,
      Integer maxWeightGrams
  ) {
    return new Discount(
        null,
        CategoryType.VEGETABLE.name(),
        DiscountType.PERCENTAGE.name(),
        null,
        null,
        null,
        null,
        minWeightGrams,
        maxWeightGrams,
        percent,
        null,
        null,
        null,
        true,
        null,
        null
    );
  }

  private Discount newBuyTakeDiscountBread(
      Integer minDayAge,
      Integer maxDayAge,
      Integer buyQty,
      Integer takeQty
  ) {
    return new Discount(
        null,
        CategoryType.BREAD.name(),
        DiscountType.BUY_TAKE.name(),
        buyQty,
        takeQty,
        minDayAge,
        maxDayAge,
        null,
        null,
        null,
        null,
        null,
        null,
        true,
        null,
        null
    );
  }

  private Discount newFixedAmountDiscountBeer(
      Integer packSize,
      BigDecimal amount
  ) {
    return new Discount(
        null,
        CategoryType.BEER.name(),
        DiscountType.FIXED_AMOUNT.name(),
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        packSize,
        null,
        amount,
        true,
        null,
        null
    );
  }

  private OrderItemRequest newOrderItemRequest(Long productId, Integer quantity,
      Integer weightGrams) {
    return new OrderItemRequest(productId, quantity, weightGrams);
  }
}
