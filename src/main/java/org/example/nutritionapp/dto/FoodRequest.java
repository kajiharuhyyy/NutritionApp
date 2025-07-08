package org.example.nutritionapp.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FoodRequest {
    @NotBlank(message = "食品名は必須です")
    private String name;

    @NotNull(message = "量は必須です")
    @Positive(message = "量は0より大きい必要があります")
    private double amount;

}
