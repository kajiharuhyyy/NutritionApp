package org.example.nutritionapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PfcRatioResponse {
  private double proteinRatio;
  private double fatRatio;
  private double carbohydrateRatio;

}
