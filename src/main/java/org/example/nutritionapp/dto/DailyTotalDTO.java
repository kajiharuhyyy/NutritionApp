package org.example.nutritionapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DailyTotalDTO {

  private LocalDate createdAt;
  private double totalEnergy;
  private double totalProtein;
  private double totalFat;
  private double totalCarbohydrates;
  private double totalSalt;

}
