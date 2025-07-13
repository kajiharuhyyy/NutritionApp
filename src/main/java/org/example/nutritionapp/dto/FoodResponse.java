package org.example.nutritionapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

import org.example.nutritionapp.model.FoodHistory;
import org.example.nutritionapp.model.FoodItem;

@Getter
@AllArgsConstructor
public class FoodResponse {
  private String name;
  private double amount;
  private double energy;
  private double protein;
  private double fat;
  private double carbohydrates;
  private double salt;

  private LocalDate createdAt;

  @SuppressWarnings("unused")
  public FoodResponse() {
  }
  public FoodHistory toHistoryEntity() {
    FoodHistory entity = new FoodHistory();
    entity.setName(this.name);
    entity.setAmount(this.amount);
    entity.setEnergy(this.energy);
    entity.setProtein(this.protein);
    entity.setFat(this.fat);
    entity.setCarbohydrates(this.carbohydrates);
    entity.setSalt(this.salt);
    entity.setCreatedAt(LocalDate.now());
    return entity;
  }

  public FoodItem toItemEntity() {
    FoodItem entity = new FoodItem();
    entity.setName(this.name);
    entity.setAmount(this.amount);
    entity.setEnergy(this.energy);
    entity.setProtein(this.protein);
    entity.setFat(this.fat);
    entity.setCarbohydrates(this.carbohydrates);
    entity.setSalt(this.salt);
    return entity;
  }
}
