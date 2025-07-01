package org.example.nutritionapp.dto;

import java.time.LocalDate;

public class DailyTotalDTO {

  private LocalDate date;
  private double totalEnergy;
  private double totalProtein;
  private double totalFat;
  private double totaCarbohydrates;
  private double totalSalt;

  public DailyTotalDTO(LocalDate date, double totalEnergy, double totalProtein, double totalFat, double totaCarbohydrates, double totalSalt) {
    this.date = date;
    this.totalEnergy = totalEnergy;
    this.totalProtein = totalProtein;
    this.totalFat = totalFat;
    this.totaCarbohydrates = totaCarbohydrates;
    this.totalSalt = totalSalt;
  }
}
