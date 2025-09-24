package com.example.survey.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.survey.model.SurveyResponse;
import com.example.survey.repository.SurveyResponseRepository;

@RestController
@RequestMapping("/api/survey")
@CrossOrigin(origins = "https://customer-survey-production.up.railway.app")
public class SurveyController {
    private static final Logger logger = LoggerFactory.getLogger(SurveyController.class);
    private final SurveyResponseRepository repository;

    public SurveyController(SurveyResponseRepository repository) {
        this.repository = repository;
    }

   /*  @PostMapping
    public SurveyResponse submitSurvey(@RequestBody SurveyResponse response) {
        return repository.save(response);
    } */
   @PostMapping
    public ResponseEntity<?> submitSurvey(@Valid @RequestBody SurveyResponse response, BindingResult result) {
        System.out.println("Received customerId: " + response.getCustomerId()); // Debug log
        logger.info("Received survey: customerId={}, projectId={}, q1={}", 
                response.getCustomerId(), response.getProjectId(), response.getQ1());
        if (result.hasErrors()) {
            logger.error("Validation errors: {}", result.getAllErrors());
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        SurveyResponse saved = repository.save(response);
        logger.info("Saved survey with ID: {}, customerId: {}, projectId: {}", saved.getId(), saved.getCustomerId(), saved.getProjectId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<SurveyResponse> getAllSurveys() {
        return repository.findAll();
    }
}