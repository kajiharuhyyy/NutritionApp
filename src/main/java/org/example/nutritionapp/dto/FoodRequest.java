package org.example.nutritionapp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FoodRequest {
    private String name;
    private double amount;

}
