package org.example.nutritionapp.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FoodRequest {
    @NotBlank(message = "{food.name.required}")
    private String name;

    @NotNull(message = "{food.amount.required}")
    @Positive(message = "{food.amount.positive}")
    private double amount;

}
