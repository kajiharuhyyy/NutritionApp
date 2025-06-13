package org.example.nutritionapp.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.example.nutritionapp.model.FoodItem;
import org.springframework.core.io.ClassPathResource;

public class FoodCsvReader {

  public static List<FoodItem> readFromCsv() {
    List<FoodItem> foodList = new ArrayList<>();

    try {
      var resource = new ClassPathResource("data/food_composition.csv");
      var reader = new BufferedReader(new InputStreamReader(resource.getInputStream(),
          StandardCharsets.UTF_8));

      String line;
      boolean isFirst = true;

      while ((line = reader.readLine()) != null) {
        if (isFirst) {
          isFirst = false;
          continue;
        }

        String[] cols = line.split(",");

        if (cols.length < 6) continue;

        String name = cols[0];
        double energy = Double.parseDouble(cols[1]);
        double protein = Double.parseDouble(cols[2]);
        double fat = Double.parseDouble(cols[3]);
        double carbohydrates = Double.parseDouble(cols[4]);
        double salt = Double.parseDouble(cols[5]);

        foodList.add(new FoodItem(name, energy, protein, fat,  carbohydrates, salt));
      }

      reader.close();

    } catch (Exception e) {
      throw new RuntimeException("CSV読み込みに失敗しました", e);
    }

    return foodList;
  }
}
