package org.example.nutritionapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleRuntime(RuntimeException e) {
    return ResponseEntity.status(404).body(e.getMessage());
  }
}
