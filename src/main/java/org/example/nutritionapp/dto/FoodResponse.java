package org.example.nutritionapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.nutritionapp.model.FoodHistory;

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

  @SuppressWarnings("unused")
  public FoodResponse() {
  }
  public FoodHistory toEntity() {
    FoodHistory entity = new FoodHistory();
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
