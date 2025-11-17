package com.store.grocerystore.repository;

import com.store.grocerystore.model.ProductDiscountEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscountEntity, Long> {

  List<ProductDiscountEntity> findByProductId(Long productId);

}

