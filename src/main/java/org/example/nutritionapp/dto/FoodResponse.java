package org.example.nutritionapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
  }
