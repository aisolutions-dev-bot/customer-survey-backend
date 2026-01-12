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
public class CarpentersEvaluation {
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
    if (carpenterLevel != null) {
      this.carpenterLevel = carpenterLevel;
      switch (carpenterLevel) {
        case "junior" -> weightedScore += (q1 * 3.0) + (q2 * 3.0) + (q3 * 3.0) +
            (q4 * 3.0) + (q5 * 1.0) + (q6 * 1.0) +
            (q7 * 3.0) + (q8 * 1.0) + (q9 * 2.0);
        case "journeyman" -> weightedScore += (q1 * 2.0) + (q2 * 2.0) + (q3 * 2.0) +
            (q4 * 2.0) + (q5 * 1.0) + (q6 * 1.0) +
            (q7 * 2.0) + (q8 * 1.0) + (q9 * 2.0) +
            (q10 * 2.0) + (q11 * 3.0);
        case "senior" -> weightedScore += (q1 * 2.0) + (q2 * 1.0) + (q3 * 1.0) +
            (q4 * 1.0) + (q5 * 1.0) + (q6 * 1.0) +
            (q7 * 4.0) + (q8 * 1.0) + (q9 * 2.0) +
            (q10 * 3.0) + (q11 * 3.0);
        default -> {
          // No additional calculation for undefined levels
        }
      }
    }
  }
}
