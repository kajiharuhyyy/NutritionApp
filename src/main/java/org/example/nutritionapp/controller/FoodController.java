package org.example.nutritionapp.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.Normalizer;

import jakarta.servlet.http.HttpServletResponse;
import org.example.nutritionapp.dto.PfcRatioResponse;
import org.example.nutritionapp.exception.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.example.nutritionapp.dto.DailyTotalDTO;
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
  public ResponseEntity<ApiResponse<List<FoodItem>>> getAllFoods() {
    List<FoodItem> foods = foodItemRepository.findAll();
    return ResponseEntity.ok(new ApiResponse<>(true, "食品一覧取得成功", foods));
  }

  @PostMapping("/calculate")
  public ResponseEntity<ApiResponse<List<FoodResponse>>> calculateFoods(@RequestBody @Valid FoodRequest request) {
    List<FoodItem> allFoods = foodItemRepository.findAll();
    String normalizedRequestName = normalize(request.getName());

    List<FoodResponse> matched = allFoods.stream()
        .filter(item -> normalize(item.getName()).contains(normalizedRequestName))
        .map(item -> {
          double factor = request.getAmount() / 100.0;
          return new FoodResponse(
              item.getName(),
              request.getAmount(),
              item.getEnergy() * factor,
              item.getProtein() * factor,
              item.getFat() * factor,
              item.getCarbohydrates() * factor,
              item.getSalt() * factor,
              null
          );
        }).collect(Collectors.toList());
    if (matched.isEmpty()) {
      throw new RuntimeException("食品が見つかりません: " + request.getName());
    }
    return ResponseEntity.ok(new ApiResponse<>(true, "計算成功", matched));
  }

  @PostMapping("/calculate-multi")
  public ResponseEntity<ApiResponse<List<FoodResponse>>> calculateMultipleFoods(@RequestBody @Valid List<@Valid FoodRequest> requests) {
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
              item.getSalt() * factor,
              null
          ));
          break;
        }
      }
    }
    return ResponseEntity.ok(new ApiResponse<>(true, "複数食品の計算成功", results));
  }

  @GetMapping("/history")
  public ResponseEntity<ApiResponse<List<FoodResponse>>> getHistory() {
    List<FoodResponse> history = foodHistoryRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
        .stream()
        .map(h -> new FoodResponse(
            h.getName(),
            h.getAmount(),
            h.getEnergy(),
            h.getProtein(),
            h.getFat(),
            h.getCarbohydrates(),
            h.getSalt(),
            h.getCreatedAt()
        ))
        .collect(Collectors.toList());

    return ResponseEntity.ok(new ApiResponse<>(true, "履歴取得成功", history));
  }

  @PostMapping("/save-result")
  public ResponseEntity<ApiResponse<String>> saveResult(@RequestBody @Valid List<FoodResponse> results) {
    List<FoodHistory> toSave = results.stream().map(FoodResponse::toHistoryEntity).toList();
    foodHistoryRepository.saveAll(toSave);
    return ResponseEntity.ok(new ApiResponse<>(true, "保存成功", "保存完了しました"));
  }

  @PostMapping("/pfc-ratio")
  public ResponseEntity<ApiResponse<PfcRatioResponse>> calculatePfcRatio(@RequestBody @Valid List<FoodResponse> foods) {
    double totalProtein = 0;
    double totalFat = 0;
    double totalCarb = 0;

    for (FoodResponse food : foods) {
      totalProtein += food.getProtein();
      totalFat += food.getFat();
      totalCarb += food.getCarbohydrates();
    }

    double proteinKcal = totalProtein * 4;
    double fatKcal = totalFat * 9;
    double carbKcal = totalCarb * 4;
    double totalKcal = proteinKcal + fatKcal + carbKcal;

    PfcRatioResponse result = (totalKcal == 0) ?
        new PfcRatioResponse(0, 0, 0) :
        new PfcRatioResponse(
          proteinKcal / totalKcal * 100,
          fatKcal / totalKcal * 100,
          carbKcal / totalKcal * 100
        );

    return ResponseEntity.ok(new ApiResponse<>(true, "PFC比計算成功", result));
  }

  @GetMapping("/history/daily-summary")
  public ResponseEntity<ApiResponse<List<DailyTotalDTO>>> getDailySummary() {
    List<DailyTotalDTO> summary = foodHistoryRepository.findDailyTotals();
    return ResponseEntity.ok(new ApiResponse<>(true, "日別計算成功", summary));
  }

  private String normalize(String input) {
    return Normalizer.normalize(input, Normalizer.Form.NFKC)
      .replaceAll("\\s+", " ")
      .trim();
  }

  @GetMapping("/export-csv")
  public ResponseEntity<byte[]> exportCsv() {
    List<FoodHistory> histiryList = foodHistoryRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

    StringBuilder csvBuilder = new StringBuilder();
    csvBuilder.append("Name,Amount,Energy,Protein,Fat,Carbohydrates,Salt,CreatedAt\n");

    for (FoodHistory h : histiryList) {
      csvBuilder.append(String.format("%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%s\n",
      h.getName(),
      h.getAmount(),
      h.getEnergy(),
      h.getProtein(),
      h.getFat(),
      h.getCarbohydrates(),
      h.getSalt(),
      h.getCreatedAt()!=null ? h.getCreatedAt().toString() : ""
      ));
    }

    byte[] csvBytes = csvBuilder.toString().getBytes();

    return ResponseEntity.ok()
    .header("Content=Disposision", "attachment; filename=food_history.csv")
    .header("Content-Type", "text/csv")
    .body(csvBytes);
  }

  @GetMapping("/export-csv")
  public void exportCsv(HttpServletResponse response) throws IOException {
    List<FoodHistory> historyList = foodHistoryRepository.findAll(Sort.by(Sort.Direction.DESC,"createdAt"));

    response.setContentType("text/csv");
    response.setHeader("Content-Disposition","attachment; filename=nutrition_history.csv");

    PrintWriter writer = response.getWriter();
    writer.println("name,amount,energy,protein,fat,carbohydrates,salt,createdAt");

    for (FoodHistory item: historyList) {
      writer.printf("%s,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%s%n",
              item.getName(),
              item.getAmount(),
              item.getEnergy(),
              item.getProtein(),
              item.getFat(),
              item.getCarbohydrates(),
              item.getSalt(),
              item.getCreatedAt()
              );
    }

    writer.flush();
    writer.close();
  }
}
