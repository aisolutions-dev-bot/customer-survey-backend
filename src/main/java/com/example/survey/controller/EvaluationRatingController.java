package com.example.survey.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.survey.dto.EvaluationRatingRequest;
import com.example.survey.model.EvaluationRating;
import com.example.survey.service.EvaluationRatingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/evaluation-ratings")
public class EvaluationRatingController {

  @Autowired
  private EvaluationRatingService service;

  /**
   * Submit a new evaluation rating
   * POST /api/evaluation-ratings
   */
  @PostMapping
  public ResponseEntity<?> submitEvaluationRating(
      @Valid @RequestBody EvaluationRatingRequest request,
      BindingResult result) {

    System.out.println("Received Evaluation Rating: " + request);

    if (result.hasErrors()) {
      return ResponseEntity.badRequest().body(result.getAllErrors());
    }

    try {
      EvaluationRating saved = service.saveEvaluationRating(request);
      System.out.println("Evaluation Rating saved with UniqId: " + saved.getUniqId());
      return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    } catch (Exception e) {
      System.err.println("Error saving evaluation rating: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error saving evaluation: " + e.getMessage());
    }
  }

  /**
   * Get all evaluation ratings
   * GET /api/evaluation-ratings
   */
  @GetMapping
  public ResponseEntity<List<EvaluationRating>> getAllEvaluations() {
    List<EvaluationRating> ratings = service.getAllRatings();
    return ResponseEntity.ok(ratings);
  }

  /**
   * Get evaluation rating by ID
   * GET /api/evaluation-ratings/{id}
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> getEvaluationById(@PathVariable Long id) {
    EvaluationRating rating = service.getRatingById(id);
    if (rating != null) {
      return ResponseEntity.ok(rating);
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("Evaluation rating not found with ID: " + id);
  }

  /**
   * Get evaluations by evaluatee ID
   * GET /api/evaluation-ratings/evaluatee/{evaluateeId}
   */
  @GetMapping("/evaluatee/{evaluateeId}")
  public ResponseEntity<List<EvaluationRating>> getEvaluationsByEvaluatee(
      @PathVariable String evaluateeId) {
    List<EvaluationRating> ratings = service.getRatingsByEvaluateeId(evaluateeId);
    return ResponseEntity.ok(ratings);
  }

  /**
   * Get evaluations by project code
   * GET /api/evaluation-ratings/project/{projectCode}
   */
  @GetMapping("/project/{projectCode}")
  public ResponseEntity<List<EvaluationRating>> getEvaluationsByProject(
      @PathVariable String projectCode) {
    List<EvaluationRating> ratings = service.getRatingsByProjectCode(projectCode);
    return ResponseEntity.ok(ratings);
  }

  /**
   * Get evaluations by evaluator ID
   * GET /api/evaluation-ratings/evaluator/{evaluatorId}
   */
  @GetMapping("/evaluator/{evaluatorId}")
  public ResponseEntity<List<EvaluationRating>> getEvaluationsByEvaluator(
      @PathVariable String evaluatorId) {
    List<EvaluationRating> ratings = service.getRatingsByEvaluatorId(evaluatorId);
    return ResponseEntity.ok(ratings);
  }

  /**
   * Get evaluations by department ID
   * GET /api/evaluation-ratings/department/{departmentId}
   */
  @GetMapping("/department/{departmentId}")
  public ResponseEntity<List<EvaluationRating>> getEvaluationsByDepartment(
      @PathVariable String departmentId) {
    List<EvaluationRating> ratings = service.getRatingsByDepartmentId(departmentId);
    return ResponseEntity.ok(ratings);
  }

  /**
   * Get evaluations by form type
   * GET /api/evaluation-ratings/form-type
   */
  @GetMapping("/form-type")
  public ResponseEntity<List<EvaluationRating>> getEvaluationsByFormType(
      @RequestParam String formType) {
    List<EvaluationRating> ratings = service.getRatingsByFormType(formType);
    return ResponseEntity.ok(ratings);
  }

  /**
   * Delete evaluation rating by ID
   * DELETE /api/evaluation-ratings/{id}
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteEvaluation(@PathVariable Long id) {
    boolean deleted = service.deleteRating(id);
    if (deleted) {
      return ResponseEntity.ok("Evaluation rating deleted successfully");
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("Evaluation rating not found with ID: " + id);
  }
}
