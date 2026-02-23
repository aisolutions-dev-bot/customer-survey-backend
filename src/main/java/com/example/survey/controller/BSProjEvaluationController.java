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

import com.example.survey.model.BSProjEvaluation;
import com.example.survey.repository.BSProjEvaluationRepository;
//import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bs-project-evaluation")
@CrossOrigin(origins = "*")  // Removed trailing slash
public class BSProjEvaluationController {
    
    private final BSProjEvaluationRepository repository;
    
    public BSProjEvaluationController(BSProjEvaluationRepository repository) {
        this.repository = repository;
    }
    
    @PostMapping
    public ResponseEntity<?> submitEvaluation(@Valid @RequestBody BSProjEvaluation evaluation, BindingResult result) {
        System.out.println("Received BS-Project Evaluation: " + evaluation);
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        
        BSProjEvaluation saved = repository.save(evaluation);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @GetMapping
    public List<BSProjEvaluation> getAllEvaluations() {
        return repository.findAll();
    }
}
