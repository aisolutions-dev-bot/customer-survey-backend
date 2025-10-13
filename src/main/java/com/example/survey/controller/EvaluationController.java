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

import com.example.survey.model.StaffEvaluation;
import com.example.survey.repository.StaffEvaluationRepository;
//import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/evaluation")
@CrossOrigin(origins = "http://localhost:4200")  // Removed trailing slash
public class EvaluationController {
    
    private final StaffEvaluationRepository repository;
    
    public EvaluationController(StaffEvaluationRepository repository) {
        this.repository = repository;
    }
    
    @PostMapping
    public ResponseEntity<?> submitEvaluation(@Valid @RequestBody StaffEvaluation evaluation, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        
        StaffEvaluation saved = repository.save(evaluation);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @GetMapping
    public List<StaffEvaluation> getAllEvaluations() {
        return repository.findAll();
    }
}