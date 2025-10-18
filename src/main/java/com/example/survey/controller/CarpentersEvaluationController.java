package com.example.survey.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.survey.model.CarpentersEvaluation;
import com.example.survey.repository.CarpentersEvaluationRepository;
//import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/carpenters-evaluation")
@CrossOrigin(origins = "*")  // Removed trailing slash
public class CarpentersEvaluationController {
    
    private final CarpentersEvaluationRepository repository;
    
    public CarpentersEvaluationController(CarpentersEvaluationRepository repository) {
        this.repository = repository;
    }
    
    @PostMapping
    public ResponseEntity<?> submitEvaluation(@Valid @RequestBody CarpentersEvaluation evaluation, BindingResult result) {
        System.out.println("Received Carpenters Evaluation: " + evaluation);
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        
        CarpentersEvaluation saved = repository.save(evaluation);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @GetMapping
    public List<CarpentersEvaluation> getAllEvaluations() {
        return repository.findAll();
    }
}
