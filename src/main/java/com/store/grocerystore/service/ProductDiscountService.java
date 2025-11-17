package com.store.grocerystore.service;


import com.store.grocerystore.controller.dto.Discount;
import com.store.grocerystore.mapper.DiscountMapper;
import com.store.grocerystore.model.ProductDiscountEntity;
import com.store.grocerystore.repository.ProductDiscountRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class ProductDiscountService {

  private final ProductDiscountRepository productDiscountRepository;

  public ProductDiscountService(ProductDiscountRepository productDiscountRepository) {
    this.productDiscountRepository = productDiscountRepository;
  }


  public List<Discount> findDiscountsForProduct(Long productId) {

    List<ProductDiscountEntity> links =
        productDiscountRepository.findByProductId(productId);

    if (links.isEmpty()) {
      return List.of();
    }

    return links.stream()
        .map(ProductDiscountEntity::getDiscount)
        .filter(Objects::nonNull)
        .map(DiscountMapper::toRecord)
        .toList();
  }


}

