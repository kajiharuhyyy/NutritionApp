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
        double amount = Double.parseDouble(cols[1]);
        double energy = parseSafe(cols[1]);
        double protein = parseSafe(cols[2]);
        double fat = parseSafe(cols[3]);
        double carbohydrates = parseSafe(cols[4]);
        double salt = parseSafe(cols[5]);

        foodList.add(new FoodItem(name, amount, energy, protein, fat,  carbohydrates, salt));
      }

      reader.close();

    } catch (Exception e) {
      throw new RuntimeException("CSV読み込みに失敗しました", e);
    }

    return foodList;
  }

//  private static double parseSafe(String value) {
//    try {
//      if (value == null || value.isBlank() || value.equalsIgnoreCase("Tr")) {
//        return 0.0;
//      }
//      return Double.parseDouble(value);
//    } catch (Exception e) {
//      return 0.0;
//    }
//  }

  private static double parseSafe(String str) {
    if (str == null || str.trim().isEmpty() || str.equalsIgnoreCase("Tr")) return 0.0;
    str = toHalfWidth(str.trim());
    try {
      return Double.parseDouble(str);
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

  private static String toHalfWidth(String input) {
    StringBuilder sb = new StringBuilder();
    for (char c : input.toCharArray()) {
      if (c >= '０' && c <= '９') {
        sb.append((char)(c - '０' + '0')); // 全角 → 半角数字
      } else if (c == '．') {
        sb.append('.'); // 全角ドット対応（念のため）
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }


}
