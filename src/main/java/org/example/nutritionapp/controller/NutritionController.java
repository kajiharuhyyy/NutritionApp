package org.example.nutritionapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NutritionController {

    @GetMapping("/nutrition")
    public String showNutritionPage() {
        return "nutrition";
    }
}
