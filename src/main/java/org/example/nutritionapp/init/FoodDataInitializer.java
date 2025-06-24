package org.example.nutritionapp.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.nutritionapp.model.FoodItem;
import org.example.nutritionapp.repository.FoodItemRepository;
import org.example.nutritionapp.util.FoodCsvReader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FoodDataInitializer {

  private final FoodItemRepository foodItemRepository;
  private FoodItem foodItem;

  @PostConstruct
  public void init() {
    if (foodItemRepository.count() == 0) {
      var csvHistory = FoodCsvReader.readFromCsv();
      foodItemRepository.saveAll(csvHistory);
      System.out.println("csvデータをDB登録しました！");
    }
  }
}
