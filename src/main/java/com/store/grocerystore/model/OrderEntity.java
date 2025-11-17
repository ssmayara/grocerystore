package com.store.grocerystore.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class OrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "total_amount")
  private BigDecimal totalAmount;

  @OneToMany(
      mappedBy = "order",
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  private List<OrderItemEntity> items = new ArrayList<>();

  @PrePersist
  public void onCreate() {
    if (this.createdAt == null) {
      this.createdAt = LocalDateTime.now();
    }
  }

  public void addItem(OrderItemEntity item) {
    items.add(item);
    item.setOrder(this);
  }

  public void removeItem(OrderItemEntity item) {
    items.remove(item);
    item.setOrder(null);
  }
}
