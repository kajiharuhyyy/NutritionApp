package org.example.nutritionapp.repository;

import java.util.List;
import java.util.Optional;
import org.example.nutritionapp.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
  Optional<FoodItem> findByNameIgnoreCase(String name);
  List<FoodItem> findAllByOrderByNameAsc();
}
