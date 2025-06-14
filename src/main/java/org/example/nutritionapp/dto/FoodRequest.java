package org.example.nutritionapp.dto;

public class FoodRequest {
    private String name;
    private double amount;

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(double amount) {
      this.amount = amount;
    }

}
