package com.example.survey.model;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class BSProjEvaluation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String staffId;
  private String projectId;
  private String roleType; // "drafter", "engineer", etc.
  private String departmentId;
  private String evaluatorId; // Unique identifier for each evaluation
  private String evaluatorName;
  private String carpenterLevel;
  private String formType;
  private String remarks;

  // Questions with weighted scores
  private Integer q1; // workProgressManagement 20%
  private Integer q2; // accuracy 30%
  private Integer q3; // jobKnowledgeSkills 20%
  private Integer q4; // teamwork 10%
  private Integer q5; // dependabilityInitiative 10%
  private Integer q6; // communicationResponsiveness 10%
  private Integer q7;
  private Integer q8;
  private Integer q9;
  private Integer q10;
  private Integer q11;
  private Double weightedScore;
  private LocalDateTime submittedAt = LocalDateTime.now();

  @PrePersist
  protected void onCreate() {
    submittedAt = LocalDateTime.now();
    // calculateWeightedScore("");
  }

  public void calculateWeightedScore(String carpenterLevel) {
    weightedScore = 0.0;
    
    // Standard form calculation (7 questions)
    // Q1: 15%, Q2: 35%, Q3: 10%, Q4: 10%, Q5: 10%, Q6: 10%, Q7: 10%
    if (q1 != null) weightedScore += (q1 * 3.0);  // 15% weight (3 points per rating)
    if (q2 != null) weightedScore += (q2 * 7.0);  // 35% weight (7 points per rating)
    if (q3 != null) weightedScore += (q3 * 2.0);  // 10% weight (2 points per rating)
    if (q4 != null) weightedScore += (q4 * 2.0);  // 10% weight (2 points per rating)
    if (q5 != null) weightedScore += (q5 * 2.0);  // 10% weight (2 points per rating)
    if (q6 != null) weightedScore += (q6 * 2.0);  // 10% weight (2 points per rating)
    if (q7 != null) weightedScore += (q7 * 2.0);  // 10% weight (2 points per rating)
    
    // Total max score: 100 (when all questions are rated 5)
    // Formula: (3*5 + 7*5 + 2*5 + 2*5 + 2*5 + 2*5 + 2*5) = 15 + 35 + 10 + 10 + 10 + 10 + 10 = 100
  }
}
