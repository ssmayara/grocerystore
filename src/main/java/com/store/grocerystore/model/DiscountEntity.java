package com.store.grocerystore.model;

import jakarta.persistence.Column;
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
@Table(name = "discount")
public class DiscountEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "product_category", nullable = false)
  private String productCategory;

  @Column(name = "rule_type", nullable = false)
  private String ruleType;

  @Column(name = "buy_qty")
  private Integer buyQty;

  @Column(name = "take_qty")
  private Integer takeQty;

  @Column(name = "min_day_age")
  private Integer minDayAge;

  @Column(name = "max_day_age")
  private Integer maxDayAge;

  @Column(name = "min_weight_grams")
  private Integer minWeightGrams;

  @Column(name = "max_weight_grams")
  private Integer maxWeightGrams;

  @Column(name = "discount_percent")
  private BigDecimal discountPercent;

  @Column(name = "pack_size")
  private Integer packSize;

  @Column(name = "beer_country")
  private String beerCountry;

  @Column(name = "discount_amount")
  private BigDecimal discountAmount;

  @Column(nullable = false)
  private Boolean active;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "update_at")
  private LocalDateTime updateAt;

  @PrePersist
  public void onCreate() {
    this.createdAt = LocalDateTime.now();
    if (this.active == null) {
      this.active = true;
    }
  }

  @PreUpdate
  public void onUpdate() {
    this.updateAt = LocalDateTime.now();
  }
}
