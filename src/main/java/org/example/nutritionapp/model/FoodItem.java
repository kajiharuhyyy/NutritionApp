package org.example.nutritionapp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodItem {
  private String name;
  private double energy;
  private double protein;
  private double fat;
  private double carbohydrates;
  private double salt;

  public FoodItem() {}

  public FoodItem(String name, double energy, double protein, double fat, double carbohydrates, double salt) {
    this.name = name;
    this.energy = energy;
    this.protein = protein;
    this.fat = fat;
    this.carbohydrates = carbohydrates;
    this.salt = salt;
  }
}
