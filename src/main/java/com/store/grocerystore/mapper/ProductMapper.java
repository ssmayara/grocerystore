package com.store.grocerystore.mapper;

import com.store.grocerystore.controller.dto.Product;
import com.store.grocerystore.enums.CategoryType;
import com.store.grocerystore.enums.UnitType;
import com.store.grocerystore.model.ProductEntity;

public class ProductMapper {

  public static ProductEntity toEntity(Product dto) {
    return new ProductEntity(
        dto.name(),
        dto.category().name(),
        dto.uniType().name(),
        dto.unitPrice()
    );
  }

  public static Product toRecord(ProductEntity entity) {
    return new Product(
        entity.getId(),
        entity.getName(),
        CategoryType.valueOf(entity.getCategory().toUpperCase()),
        UnitType.valueOf(entity.getUnitType().toUpperCase()),
        entity.getUnitPrice()
    );
  }
}
