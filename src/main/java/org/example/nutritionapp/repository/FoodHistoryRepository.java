package org.example.nutritionapp.repository;

import java.util.List;
import java.util.Optional;
import org.example.nutritionapp.model.FoodHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodHistoryRepository extends JpaRepository<FoodHistory, Long> {
    Optional<FoodHistory> findByNameIgnoreCase(String name);
    List<FoodHistory> findAllByOrderByCreatedAtDesc();
  }

