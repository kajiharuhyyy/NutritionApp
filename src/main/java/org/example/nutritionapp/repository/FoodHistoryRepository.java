package org.example.nutritionapp.repository;

import java.util.List;
import org.example.nutritionapp.model.FoodHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodHistoryRepository extends JpaRepository<FoodHistory, Long> {
  List<FoodHistory> findAllByOrderByCreatedAtDesc();
}
