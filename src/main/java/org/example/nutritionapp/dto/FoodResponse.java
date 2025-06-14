package org.example.nutritionapp.dto;

import org.example.nutritionapp.util.FoodCsvReader;

public class FoodResponse {
  private String name;
  private double amount;
  private double energy;
  private double protein;
  private double fat;
  private double carbohydrates;
  private double salt;

  public FoodResponse(String name, double amount, double energy, double protein, double fat, double carbohydrates, double salt) {
    this.name = name;
    this.amount = amount;
    this.energy = energy;
    this.protein = protein;
    this.fat = fat;
    this.carbohydrates = carbohydrates;
    this.salt = salt;
  }

}
