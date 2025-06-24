package org.example.nutritionapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FoodItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private double amount;
  private double energy;
  private double protein;
  private double fat;
  private double carbohydrates;
  private double salt;

  // JPA用のデフォルトコンストラクタ
  public FoodItem() {}

  // 任意で使う全フィールドのコンストラクタ
  public FoodItem(String name, double amount, double energy, double protein, double fat, double carbohydrates, double salt) {
    this.name = name;
    this.amount = amount;
    this.energy = energy;
    this.protein = protein;
    this.fat = fat;
    this.carbohydrates = carbohydrates;
    this.salt = salt;
  }
}
