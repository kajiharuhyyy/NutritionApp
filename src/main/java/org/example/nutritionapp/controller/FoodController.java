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
import org.example.nutritionapp.model.FoodItem;
import org.example.nutritionapp.model.FoodHistory;
import org.example.nutritionapp.repository.FoodHistoryRepository;
import org.example.nutritionapp.repository.FoodItemRepository;

@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = "*")
public class FoodController {

  @Autowired
  private FoodItemRepository foodItemRepository;

  @Autowired
  private FoodHistoryRepository foodHistoryRepository;

  @GetMapping
  public List<FoodItem> getAllFoods() {
    return foodItemRepository.findAll();
  }

  @PostMapping("/calculate")
  public FoodResponse calculateFoods(@RequestBody FoodRequest request) {
    List<FoodItem> allFoods = foodItemRepository.findAll(); // CSVではなくDBから取得

    String normalizedRequestName = normalize(request.getName());

    for (FoodItem item : allFoods) {
      if (normalize(item.getName()).equalsIgnoreCase(normalizedRequestName)) {
        double factor = request.getAmount() / 100.0;

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
    throw new RuntimeException("Food Not Found: " + request.getName());
  }

  @PostMapping("/calculate-multi")
  public List<FoodResponse> calculateMultipleFoods(@RequestBody List<FoodRequest> requests) {
    List<FoodItem> allFoods = foodItemRepository.findAll();
    List<FoodResponse> results = new ArrayList<>();

    for (FoodRequest request : requests) {
      String normalizedRequestName = normalize(request.getName());

      for (FoodItem item : allFoods) {
        if (normalize(item.getName()).equalsIgnoreCase(normalizedRequestName)) {
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
    return foodHistoryRepository.findAllByOrderByCreatedAtDesc()
        .stream()
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
    List<FoodHistory> toSave = results.stream().map(FoodResponse::toHistoryEntity).toList();
    foodHistoryRepository.saveAll(toSave);
    return ResponseEntity.ok("保存成功");
  }

  private String normalize(String input) {
    return Normalizer.normalize(input, Normalizer.Form.NFKC)
        .replaceAll("　", " ")
        .trim();
  }
}
