package com.store.grocerystore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product")
public class ProductEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String category;
  private String unitType;
  private BigDecimal unitPrice;
  private LocalDateTime createdAt;
  private LocalDateTime updateAt;

  public ProductEntity() {
  }

  public ProductEntity(Long id, String name, String category, String unitType,
      BigDecimal unitPrice, LocalDateTime createdAt, LocalDateTime updateAt) {
    this.id = id;
    this.name = name;
    this.category = category;
    this.unitType = unitType;
    this.unitPrice = unitPrice;
    this.createdAt = createdAt;
    this.updateAt = updateAt;
  }

  public ProductEntity(String name, String category, String unitType, BigDecimal unitPrice) {
    this.name = name;
    this.category = category;
    this.unitType = unitType;
    this.unitPrice = unitPrice;
  }

  @PrePersist
  public void onCreate() {
    createdAt = LocalDateTime.now();
  }

  @PreUpdate
  public void onUpdate() {
    updateAt = LocalDateTime.now();
  }
}
