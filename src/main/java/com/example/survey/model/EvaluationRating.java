package com.example.survey.model;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Column;


@Entity
@Table(name = "m17EvaluationRatings")

public class EvaluationRating {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "UniqId")
  private Long uniqId;

  @Column(name = "EvaluatorId", length = 25)
  private String evaluatorId;

  @Column(name = "ProjectCode", length = 25)
  private String projectCode;

  @Column(name = "EvaluateeId", length = 25)
  private String evaluateeId;

  @Column(name = "FormType", length = 50)
  private String formType; // CARPENTER, BS-PROJECT, BS-TENDER, DRAFTER, etc.

  @Column(name = "SkillSet", length = 50)
  private String skillSet;

  @Column(name = "WeightedScore")
  private Double weightedScore;

  @Column(name = "SubmittedAt")
  private LocalDateTime submittedAt;

  @Column(name = "DepartmentId", length = 25)
  private String departmentId;

  @Column(name = "Remarks", length = 8000)
  private String remarks;

  @Column(name = "q1")
  private Integer q1;

  @Column(name = "q2")
  private Integer q2;

  @Column(name = "q3")
  private Integer q3;

  @Column(name = "q4")
  private Integer q4;

  @Column(name = "q5")
  private Integer q5;

  @Column(name = "q6")
  private Integer q6;

  @Column(name = "q7")
  private Integer q7;

  @Column(name = "q8")
  private Integer q8;

  @Column(name = "q9")
  private Integer q9;

  @Column(name = "q10")
  private Integer q10;

  @Column(name = "q11")
  private Integer q11;

  // Constructors
  public EvaluationRating() {
  }

  @PrePersist
  protected void onCreate() {
    if (submittedAt == null) {
      submittedAt = LocalDateTime.now();
    }
  }

  /**
   * Calculate weighted score based on formType
   * Each form type has different number of questions and different weights
   */
  public void calculateWeightedScore() {
    if (formType == null || formType.trim().isEmpty()) {
        formType = "CARPENTER"; // Default fallback
      return;
    }

    weightedScore = 0.0;
    switch (formType.toUpperCase()) {
      case "CARPENTER":
        calculateCarpenterScore();
        break;
      case "BS-PROJECT":
        calculateBsProjectScore();
        break;
      case "BS-TENDER":
        calculateBsTenderScore();
        break;
      case "DRAFTER":
        calculateDrafterScore();
        break;
      case "OPERATION":
        calculateOperationScore();
        break;
      default:
        // For unknown form types, use a generic calculation
        calculateGenericScore();
        break;
    } 
  }

  /**
   * CARPENTER scoring (7 questions)
   * Q1=15%, Q2=35%, Q3=10%, Q4=20%, Q5=10%, Q6=10%, Q7=5%
   * NOTE: IGNORE CAPRENTER category scoring, it is still using the original scoring in "CarpenterEvaluation.java" class
   */
  private void calculateCarpenterScore() {
    double[] weights = {15.0, 35.0, 10.0, 20.0, 10.0, 10.0, 5.0};
    Integer[] answers = {q1, q2, q3, q4, q5, q6, q7};
    
    for (int i = 0; i < answers.length && i < weights.length; i++) {
      if (answers[i] != null) {
        // Convert rating (1-5) to percentage of weight
        weightedScore += (answers[i] / 5.0) * weights[i];
      }
    }
  }

  /**
   * Business Support- PROJECT scoring (7 questions)
   * Q1=15%, Q2=35%, Q3=10%, Q4=10%, Q5=10%, Q6=10%, Q7=10%
   */
  private void calculateBsProjectScore() {
    double[] weights = {15.0, 35.0, 10.0, 10.0, 10.0, 10.0, 10.0};
    Integer[] answers = {q1, q2, q3, q4, q5, q6, q7};
    
    for (int i = 0; i < answers.length && i < weights.length; i++) {
      if (answers[i] != null) {
        // Convert rating (1-5) to percentage of weight
        weightedScore += (answers[i] / 5.0) * weights[i];
      }
    }
  }

  /**
   * Business Support- TENDER scoring (6 questions)
   * Q1=30%, Q2=30%, Q3=20%, Q4=10%, Q5=10%
   */
  private void calculateBsTenderScore() {
    double[] weights = {30.0, 30.0, 20.0, 10.0, 10.0};
    Integer[] answers = {q1, q2, q3, q4, q5};
    
    for (int i = 0; i < answers.length && i < weights.length; i++) {
      if (answers[i] != null) {
        // Convert rating (1-5) to percentage of weight
        weightedScore += (answers[i] / 5.0) * weights[i];
      }
    }
  }

  /**
   * DRAFTER scoring (6 questions)
   * Q1=20%, Q2=30%, Q3=20%, Q4=10%, Q5=10%, Q6=10%
   */
  private void calculateDrafterScore() {
    double[] weights = {20.0, 30.0, 20.0, 10.0, 10.0, 10.0};
    Integer[] answers = {q1, q2, q3, q4, q5, q6};
    
    for (int i = 0; i < answers.length && i < weights.length; i++) {
      if (answers[i] != null) {
        // Convert rating (1-5) to percentage of weight
        weightedScore += (answers[i] / 5.0) * weights[i];
      }
    }
  }

  /**
   * OPERATION scoring (6 questions)
   * Q1=30%, Q2=30%, Q3=10%, Q4=10%, Q5=10%, Q6=10%
   */
  private void calculateOperationScore() {
    double[] weights = {30.0, 30.0, 10.0, 10.0, 10.0, 10.0};
    Integer[] answers = {q1, q2, q3, q4, q5, q6};
    
    for (int i = 0; i < answers.length && i < weights.length; i++) {
      if (answers[i] != null) {
        // Convert rating (1-5) to percentage of weight
        weightedScore += (answers[i] / 5.0) * weights[i];
      }
    }
  }

  /**
   * Generic scoring for unknown form types
   */
  private void calculateGenericScore() {
    double[] weights = {10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 10.0};
    Integer[] answers = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10};
    
    for (int i = 0; i < answers.length && i < weights.length; i++) {
      if (answers[i] != null) {
        // Convert rating (1-5) to percentage of weight
        weightedScore += (answers[i] / 5.0) * weights[i];
      }
    }
  }

  // Getters and Setters
  public Long getUniqId() {
    return uniqId;
  }

  public void setUniqId(Long uniqId) {
    this.uniqId = uniqId;
  }

  public String getEvaluatorId() {
    return evaluatorId;
  }

  public void setEvaluatorId(String evaluatorId) {
    this.evaluatorId = evaluatorId;
  }

  public String getProjectCode() {
    return projectCode;
  }

  public void setProjectCode(String projectCode) {
    this.projectCode = projectCode;
  }

  public String getEvaluateeId() {
    return evaluateeId;
  }

  public void setEvaluateeId(String evaluateeId) {
    this.evaluateeId = evaluateeId;
  }

  public String getFormType() {
    return formType;
  }

  public void setFormType(String formType) {
    this.formType = formType;
  }

  public String getSkillSet() {
    return skillSet;
  }

  public void setSkillSet(String skillSet) {
    this.skillSet = skillSet;
  }

  public Double getWeightedScore() {
    return weightedScore;
  }

  public void setWeightedScore(Double weightedScore) {
    this.weightedScore = weightedScore;
  }

  public LocalDateTime getSubmittedAt() {
    return submittedAt;
  }

  public void setSubmittedAt(LocalDateTime submittedAt) {
    this.submittedAt = submittedAt;
  }

  public String getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(String departmentId) {
    this.departmentId = departmentId;
  }

  public String getRemarks() {
    return remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public Integer getQ1() {
    return q1;
  }

  public void setQ1(Integer q1) {
    this.q1 = q1;
  }

  public Integer getQ2() {
    return q2;
  }

  public void setQ2(Integer q2) {
    this.q2 = q2;
  }

  public Integer getQ3() {
    return q3;
  }

  public void setQ3(Integer q3) {
    this.q3 = q3;
  }

  public Integer getQ4() {
    return q4;
  }

  public void setQ4(Integer q4) {
    this.q4 = q4;
  }

  public Integer getQ5() {
    return q5;
  }

  public void setQ5(Integer q5) {
    this.q5 = q5;
  }

  public Integer getQ6() {
    return q6;
  }

  public void setQ6(Integer q6) {
    this.q6 = q6;
  }

  public Integer getQ7() {
    return q7;
  }

  public void setQ7(Integer q7) {
    this.q7 = q7;
  }

  public Integer getQ8() {
    return q8;
  }

  public void setQ8(Integer q8) {
    this.q8 = q8;
  }

  public Integer getQ9() {
    return q9;
  }

  public void setQ9(Integer q9) {
    this.q9 = q9;
  }

  public Integer getQ10() {
    return q10;
  }

  public void setQ10(Integer q10) {
    this.q10 = q10;
  }

  public Integer getQ11() {
    return q11;
  }

  public void setQ11(Integer q11) {
    this.q11 = q11;
  }

  @Override
  public String toString() {
    return "EvaluationRating{" +
        "uniqId=" + uniqId +
        ", evaluatorId='" + evaluatorId + '\'' +
        ", projectCode='" + projectCode + '\'' +
        ", evaluateeId='" + evaluateeId + '\'' +
        ", formType='" + formType + '\'' +
        ", skillSet='" + skillSet + '\'' +
        ", weightedScore=" + weightedScore +
        ", submittedAt=" + submittedAt +
        ", departmentId='" + departmentId + '\'' +
        '}';
  }
}
