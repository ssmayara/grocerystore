package com.store.grocerystore.exceptions;

public class OrderValidationException extends RuntimeException {

  public OrderValidationException(String message) {
    super(message);
  }
}
