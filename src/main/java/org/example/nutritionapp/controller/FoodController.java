package org.example.nutritionapp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import org.example.nutritionapp.dto.FoodRequest;
import org.example.nutritionapp.dto.FoodResponse;
import org.example.nutritionapp.model.FoodItem;
import org.example.nutritionapp.util.FoodCsvReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = "*")
public class FoodController {

  @GetMapping
  public List<FoodItem> getAllFoods() {
    return FoodCsvReader.readFromCsv();
  }

  @PostMapping("/calculate")
  public FoodResponse calculateFoods(@RequestBody FoodRequest request) {
    List<FoodItem> allFoods = FoodCsvReader.readFromCsv();
    System.out.println("★ リクエストされた食品名: " + request.getName());

    for (FoodItem item : allFoods) {
      System.out.println("CSV読み込み食品名: " + item.getName());
      String normalizedItemName = Normalizer.normalize(item.getName(), Normalizer.Form.NFKC)
          .replaceAll("　", " ")
          .trim();
      String normalizedRequestName = Normalizer.normalize(request.getName(), Normalizer.Form.NFKC)
          .replaceAll("　", " ")
          .trim();

      if (normalizedItemName.equalsIgnoreCase(normalizedRequestName)) {
        double factor = request.getAmount() / 100.0;
        System.out.println("元のCSV名: [" + item.getName() + "]");
        System.out.println("正規化後CSV名: [" + normalizedItemName + "]");
        System.out.println("リクエスト名: [" + request.getName() + "]");
        System.out.println("正規化後リクエスト名: [" + normalizedRequestName + "]");
        System.out.println("normalizedItemName: [" + normalizedItemName + "]");
        System.out.println("normalizedRequestName: [" + normalizedRequestName + "]");
        System.out.println("一致判定: " + normalizedItemName.equalsIgnoreCase(normalizedRequestName));


        return new FoodResponse(
            item.getName(),
            request.getAmount(),
            item.getEnergy() * factor,
            item.getProtein() * factor,
            item.getFat() * factor,
            item.getCarbohydrates() * factor,
            item.getSalt() * factor
        );
      }

    }
    throw new RuntimeException("Food Not Found" + request.getName());
  }

  @PostMapping("/calculate-multi")
  public List<FoodResponse> calculateMultipleFoods(@RequestBody List<FoodRequest> requests) {
    List<FoodItem> allFoods = FoodCsvReader.readFromCsv();
    List<FoodResponse> results = new ArrayList<>();

    for (FoodRequest request : requests) {
      String normalizedRequestName = Normalizer.normalize(request.getName(), Normalizer.Form.NFKC)
          .replaceAll("[<UNK>]", " ")
          .trim();

      for (FoodItem item : allFoods) {
        String normalizedItemName = Normalizer.normalize(item.getName(), Normalizer.Form.NFKC)
            .replaceAll("[<UNK>]", " ")
            .trim();

        if (normalizedItemName.equalsIgnoreCase(normalizedRequestName)) {
          double factor = request.getAmount() / 100.0;

          results.add(new FoodResponse(
              item.getName(),
              request.getAmount(),
              item.getEnergy() * factor,
              item.getProtein() * factor,
              item.getFat() * factor,
              item.getCarbohydrates() * factor,
              item.getSalt() * factor
          ));
          break;
        }
      }
    }
    return results;
  }

  @PostMapping("/save-result")
  public ResponseEntity<String> saveResult(@RequestBody List<FoodResponse> results) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      Path path = Paths.get("saved_results.json");
      List<FoodResponse> allResults = new ArrayList<>();

      if (Files.exists(path)) {
        String existingJson = Files.readString(path);
        allResults = mapper.readValue(existingJson, new TypeReference<>() {});
      }

      allResults.addAll(results);
      Files.write(path, mapper.writeValueAsBytes(allResults));

      return ResponseEntity.ok("保存成功");
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("保存失敗");
    }
  }

  @GetMapping("/history")
  public List<FoodResponse> getHistory() throws IOException {
    File file = new File("saved_results.json");
    if (!file.exists()) return List.of();

    String json = Files.readString(file.toPath());
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(json, new TypeReference<>() {
    });
  }
}
