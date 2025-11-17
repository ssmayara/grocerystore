package com.store.grocerystore.repository;

import com.store.grocerystore.model.DiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<DiscountEntity, Long> {

}
