package org.example.nutritionapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class FoodHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;
  private double amount;
  private double energy;
  private double protein;
  private double fat;
  private double carbohydrates;
  private double salt;

  @Column(nullable = false, updatable = false)
  private LocalDate createdAt;

  @PrePersist
  public void onCreate() {
    this.createdAt = LocalDate.now();
  }

  // private LocalDateTime createdAt = LocalDateTime.now();

  // private LocalDate date = LocalDate.now();

  // デフォルトコンストラクタ（JPA用）
  public FoodHistory() {}

  // 必要があれば全フィールド用コンストラクタ
  public FoodHistory(String name, double amount, double energy, double protein, double fat, double carbohydrates, double salt) {
    this.name = name;
    this.amount = amount;
    this.energy = energy;
    this.protein = protein;
    this.fat = fat;
    this.carbohydrates = carbohydrates;
    this.salt = salt;
  }
}