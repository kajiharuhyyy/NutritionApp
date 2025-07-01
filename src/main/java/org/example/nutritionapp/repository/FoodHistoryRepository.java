package org.example.nutritionapp.repository;

import java.util.List;
import java.util.Optional;

import org.example.nutritionapp.dto.DailyTotalDTO;
import org.example.nutritionapp.model.FoodHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FoodHistoryRepository extends JpaRepository<FoodHistory, Long> {

    Optional<FoodHistory> findByNameIgnoreCase(String name);
    List<FoodHistory> findAllByOrderByCreatedAtDesc();

    @Query("SELECT NEW org.example.nutritionapp.dto.DailyTotalDTO(f.date, SUM(f.energy), SUM(f.protein), SUM(f.fat), SUM(f.carbohydrates), SUM(f.salt)) " +
       "FROM FoodHistory f GROUP BY f.date ORDER BY f.date")
    List<DailyTotalDTO> findDailyTotals();
  }