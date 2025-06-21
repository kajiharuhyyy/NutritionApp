package org.example.nutritionapp.controller;

import static org.example.nutritionapp.util.FoodCsvReader.readFromCsv;

import java.text.Normalizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.example.nutritionapp.dto.FoodRequest;
import org.example.nutritionapp.dto.FoodResponse;
import org.example.nutritionapp.model.FoodHistory;
import org.example.nutritionapp.model.FoodItem;
import org.example.nutritionapp.repository.FoodHistoryRepository;

@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = "*")
public class FoodController {

  @Autowired
  private FoodHistoryRepository historyRepository;

  @GetMapping
  public List<FoodItem> getAllFoods() {
    return readFromCsv();
  }


  @PostMapping("/calculate")
  public FoodResponse calculateFoods(@RequestBody FoodRequest request) {
    List<FoodItem> allFoods = readFromCsv();
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
    List<FoodItem> allFoods = readFromCsv();
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

  @GetMapping("/history")
  public List<FoodResponse> getHistory() {
        return historyRepository.findAllByOrderByCreatedAtDesc().stream()
      .map(history -> new FoodResponse(
      history.getName(),
          history.getAmount(),
              history.getEnergy(),
              history.getProtein(),
              history.getFat(),
              history.getCarbohydrates(),
              history.getSalt()
              ))
              .collect(Collectors.toList());
}

  @PostMapping("/save-result")
  public ResponseEntity<?> saveResult(@RequestBody List<FoodResponse> results) {
    List<FoodHistory> toSave = results.stream().map(FoodResponse::toEntity).toList();
    historyRepository.saveAll(toSave);
    return ResponseEntity.ok("保存成功");
  }
}
