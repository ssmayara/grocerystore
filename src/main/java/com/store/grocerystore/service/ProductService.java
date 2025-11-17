package com.store.grocerystore.service;

import com.store.grocerystore.controller.dto.Product;
import com.store.grocerystore.exceptions.ProductNotFoundException;
import com.store.grocerystore.mapper.ProductMapper;
import com.store.grocerystore.model.ProductEntity;
import com.store.grocerystore.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public Product save(Product product) {
    ProductEntity entity = ProductMapper.toEntity(product);
    ProductEntity saved = productRepository.save(entity);
    return ProductMapper.toRecord(saved);
  }

  public List<Product> findAll() {
    return productRepository.findAll()
        .stream()
        .map(ProductMapper::toRecord)
        .toList();
  }

  public Product findById(Long id) {
    return productRepository.findById(id)
        .map(ProductMapper::toRecord)
        .orElseThrow(() -> new ProductNotFoundException(id));
  }

  public ProductEntity findEntityById(Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
  }
}

