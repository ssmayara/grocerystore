package com.store.grocerystore.service;


import com.store.grocerystore.controller.dto.Discount;
import com.store.grocerystore.enums.CategoryType;
import com.store.grocerystore.enums.DiscountType;
import com.store.grocerystore.mapper.DiscountMapper;
import com.store.grocerystore.model.DiscountEntity;
import com.store.grocerystore.repository.DiscountRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {

  private final DiscountRepository discountRepository;

  public DiscountService(DiscountRepository discountRepository) {
    this.discountRepository = discountRepository;
  }


  public List<DiscountEntity> findAll() {
    return discountRepository.findAll();
  }

  public Optional<DiscountEntity> findById(Long id) {
    return discountRepository.findById(id);
  }

  public DiscountEntity save(DiscountEntity discount) {
    return discountRepository.save(discount);
  }

  public void deleteById(Long id) {
    discountRepository.deleteById(id);
  }

  public List<Discount> findBreadDiscountsForAge(int daysOld) {
    return discountRepository.findAll().stream()
        .map(DiscountMapper::toRecord)
        .filter(d -> (d.active()) != null && d.active())
        .filter(d -> CategoryType.BREAD.name().equalsIgnoreCase(d.productCategory()))
        .filter(d -> DiscountType.BUY_TAKE.name().equalsIgnoreCase(d.ruleType()))
        .filter(d -> d.minDayAge() != null && d.maxDayAge() != null)
        .filter(d -> daysOld >= d.minDayAge() && daysOld <= d.maxDayAge())
        .toList();
  }

  public Optional<Discount> findVegetableDiscountForWeight(int totalWeightGrams) {
    return discountRepository.findAll().stream()
        .map(DiscountMapper::toRecord)
        .filter(d -> d.active() != null && d.active())
        .filter(d -> CategoryType.VEGETABLE.name().equalsIgnoreCase(d.productCategory()))
        .filter(d -> DiscountType.PERCENTAGE.name().equalsIgnoreCase(d.ruleType()))
        .filter(d -> d.minWeightGrams() != null && d.maxWeightGrams() != null)
        .filter(d -> totalWeightGrams >= d.minWeightGrams()
            && totalWeightGrams <= d.maxWeightGrams())
        .sorted((a, b) -> b.discountPercent().compareTo(a.discountPercent()))
        .findFirst();
  }

  public Optional<Discount> findBeerPackDiscount(String country, int packSize) {
    return discountRepository.findAll().stream()
        .map(DiscountMapper::toRecord)
        .filter(d -> d.active() != null && d.active())
        .filter(d -> CategoryType.BEER.name().equalsIgnoreCase(d.productCategory()))
        .filter(d -> DiscountType.FIXED_AMOUNT.name().equalsIgnoreCase(d.ruleType()))
        .filter(d -> d.packSize() != null && d.packSize() == packSize)
        .filter(d -> d.beerCountry() != null
            && d.beerCountry().equalsIgnoreCase(country))
        .findFirst();
  }
}
