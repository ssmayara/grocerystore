package com.store.grocerystore.exceptions;

public class ProductPriceNotConfiguredException extends RuntimeException {

  public ProductPriceNotConfiguredException(Long productId) {
    super("Product price not configured for product id " + productId);
  }
}

