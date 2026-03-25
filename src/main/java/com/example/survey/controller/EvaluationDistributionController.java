package com.example.survey.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.survey.dto.EvaluationDistributionFullDTO;
import com.example.survey.model.EvaluationDistribution;
import com.example.survey.service.EvaluationDistributionService;

@RestController
@RequestMapping("/api/evaluation-distributions")
public class EvaluationDistributionController {

  @Autowired
  private EvaluationDistributionService evaluationDistributionService;

  /**
   * GET /api/evaluation-distributions/{uniqId}
   * Get evaluation distribution by uniqId
   */
  @GetMapping("/{uniqId}")
  public ResponseEntity<EvaluationDistribution> getByUniqId(@PathVariable Integer uniqId) {
    try {
      EvaluationDistribution distribution = evaluationDistributionService.getByUniqId(uniqId);

      if (distribution == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(null);
      }

      return ResponseEntity.ok(distribution);

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(null);
    }
  }

  /**
   * PATCH /api/evaluation-distributions/{uniqId}/status
   * Update the status of an evaluation distribution
   * Request body should contain: { "status": "SUBMITTED" }
   */
  @PatchMapping("/{uniqId}/status")
  public ResponseEntity<EvaluationDistribution> updateStatus(
      @PathVariable Integer uniqId,
      @RequestBody Map<String, String> statusUpdate) {
    try {
      String newStatus = statusUpdate.get("status");

      if (newStatus == null || newStatus.trim().isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(null);
      }

      EvaluationDistribution updatedDistribution = evaluationDistributionService.updateStatus(uniqId, newStatus.trim());

      if (updatedDistribution == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(null);
      }

      return ResponseEntity.ok(updatedDistribution);

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(null);
    }
  }

  /**
   * GET /api/evaluation-distributions/{uniqId}
   * Get evaluation distribution by uniqId
   */
  @GetMapping("/group-id/{groupId}")
  public ResponseEntity<List<EvaluationDistributionFullDTO>> getPendingByGroupId(@PathVariable Integer groupId) {
    try {
      List<EvaluationDistributionFullDTO> distribution = evaluationDistributionService.getPendingByUniqId(groupId);

      if (distribution == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(null);
      }

      return ResponseEntity.ok(distribution);

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(null);
    }
  }
}
