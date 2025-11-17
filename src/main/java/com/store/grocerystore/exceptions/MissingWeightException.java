package com.store.grocerystore.exceptions;

public class MissingWeightException extends RuntimeException {

  public MissingWeightException() {
    super("weightGrams is required for VEGETABLE products");
  }

  public MissingWeightException(String message) {
    super(message);
  }
}
