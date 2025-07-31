package org.example.nutritionapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class PageController {

    @GetMapping("/nutrition")
    public String showNutritionPage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "nutrition.html";
    }

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/login";
    }
}
