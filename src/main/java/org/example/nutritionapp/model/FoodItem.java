package org.example.nutritionapp.model;

public class FoodItem {

  private String name;
  private double energy;
  private double protein;
  private double fat;
  private double carbohydrates;
  private double salt;

  public FoodItem(String name, double energy, double protein, double fat, double carbohydrates, double salt) {
    this.name = name;
    this.energy = energy;
    this.protein = protein;
    this.fat = fat;
    this.carbohydrates = carbohydrates;
    this.salt = salt;
  }

  public  String getName() {
    return name;
  }

  public double getEnergy() {
    return energy;
  }

  public double getProtein() {
    return protein;
  }

  public double getFat() {
    return fat;
  }

  public double getCarbohydrates() {
    return carbohydrates;
  }

  public double getSalt() {
    return salt;
  }

}
