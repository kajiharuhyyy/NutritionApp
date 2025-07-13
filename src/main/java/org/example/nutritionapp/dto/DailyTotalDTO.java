package org.example.nutritionapp.dto;

import java.time.LocalDate;

public class DailyTotalDTO {

  private LocalDate date;
  private double totalEnergy;
  private double totalProtein;
  private double totalFat;
  private double totalCarbohydrates;
  private double totalSalt;

  public DailyTotalDTO(LocalDate createdAt, double energy, double protein, double fat, double carbohydrates, double salt) {
    this.date = createdAt;
    this.totalEnergy = energy;
    this.totalProtein = protein;
    this.totalFat = fat;
    this.totalCarbohydrates = carbohydrates;
    this.totalSalt = salt;
  }
}
