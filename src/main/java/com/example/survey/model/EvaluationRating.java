package com.example.survey.model;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.aisolutions.shared.util.DateUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;


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

  @Column(name = "DuplicationSourceId", length = 25)
  private String duplicationSourceId;

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

  @Column(name = "q12")
  private Integer q12;

  @Column(name = "q13")
  private Integer q13;

  @Column(name = "q14")
  private Integer q14;

  @Column(name = "q15")
  private Integer q15;

  @Column(name = "q16")
  private Integer q16;

  @Column(name = "q17")
  private Integer q17;

  @Column(name = "q18")
  private Integer q18;

  @Column(name = "q19")
  private Integer q19;

  @Column(name = "q20")
  private Integer q20;

  // Constructors
  public EvaluationRating() {
  }

    @PrePersist
    public void prePersist() {
        if (submittedAt == null) {
            submittedAt = DateUtil.nowSGT();
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
      case "CEILING":
        calculateCeilingScore(this.skillSet);
        break;
      case "SPRAYPAINT":
         calculateSpraypaintScore(this.skillSet);
        break;
      case "METAL":
         calculateMetalScore(this.skillSet);
        break;
      case "METALCUT":
         calculateMetalCutScore(this.skillSet);
        break;
      case "ME-PROJECT":
         calculateMeProjectScore(this.skillSet);
        break;
      case "TEAM-D":
         calculateTeamDScore(this.skillSet);
        break;
      case "PM-EVALUATION":
        calculatePmEvaluationScore();
        break;
      default:
        // For unknown form types, use a generic calculation
        calculateGenericScore();
        break;
    } 
  }

  /**
   * CARPENTER scoring with level-specific weights
   * Junior: 9 questions, Journeyman: 11 questions, Senior: 11 questions
   * Uses normalization formula: (Σ answer × weight) / (5 × Σ weight) × 100
   * skillSet field stores the level ("JUNIOR", "JOURNEYMAN", "SENIOR")
   */
  private void calculateCarpenterScore() {
    double[] weights;
    Integer[] answers;
    
    String level = this.skillSet != null ? this.skillSet.toUpperCase() : "";
    
    switch (level) {
      case "JUNIOR":
        weights = new double[]{15.0, 15.0, 15.0, 15.0, 5.0, 5.0, 15.0, 5.0, 10.0};
        answers = new Integer[]{q1, q2, q3, q4, q5, q6, q7, q8, q9};
        break;
      case "JOURNEYMAN":
        weights = new double[]{10.0, 10.0, 10.0, 10.0, 5.0, 5.0, 10.0, 5.0, 10.0, 10.0, 15.0};
        answers = new Integer[]{q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11};
        break;
      case "SENIOR":
        weights = new double[]{10.0, 5.0, 5.0, 5.0, 5.0, 5.0, 20.0, 5.0, 10.0, 15.0, 15.0};
        answers = new Integer[]{q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11};
        break;
      default:
        // Fallback to junior weights if level unknown
        weights = new double[]{15.0, 15.0, 15.0, 15.0, 5.0, 5.0, 15.0, 5.0, 10.0};
        answers = new Integer[]{q1, q2, q3, q4, q5, q6, q7, q8, q9};
        break;
    }
    
    double rawScore = 0.0;
    double totalWeight = 0.0;
    for (int i = 0; i < answers.length; i++) {
      if (answers[i] != null) {
        rawScore += answers[i] * weights[i];
        totalWeight += weights[i];
      }
    }
    
    double maxScore = 5.0 * totalWeight;
    if (maxScore > 0) {
      weightedScore = (double) Math.round((rawScore / maxScore) * 100.0);
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
   * CEILING scoring (Level1 9 questions, Level2 10 questions, Level3 10 questions)
   */
  private void calculateCeilingScore(String skillSet) {
    weightedScore = 0.0;
    if (skillSet != null) {
      this.skillSet = skillSet;
      switch (skillSet) {
        case "level1" : 
          double[] weights1 = {15.0, 15.0, 15.0, 15.0, 5.0, 5.0, 15.0, 5.0, 10.0};
          Integer[] answers1 = {q1, q2, q3, q4, q5, q6, q7, q8, q9};
      
          for (int i = 0; i < answers1.length && i < weights1.length; i++) {
            if (answers1[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers1[i] / 5.0) * weights1[i];
            }
          }
          break;
        case "level2" :
           double[] weights2 = {10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 5.0, 10.0, 10.0, 15.0};
          Integer[] answers2 = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10};
      
          for (int i = 0; i < answers2.length && i < weights2.length; i++) {
            if (answers2[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers2[i] / 5.0) * weights2[i];
            }
          }
          break;
        case "level3" :
           double[] weights3 = {10.0, 5.0, 10.0, 5.0, 5.0, 20.0, 5.0, 10.0, 15.0, 15.0};
          Integer[] answers3 = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10};
      
          for (int i = 0; i < answers3.length && i < weights3.length; i++) {
            if (answers3[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers3[i] / 5.0) * weights3[i];
            }
          }
        default : {
          // No additional calculation for undefined levels
        }
      }
    }
  }

  /**
   * SPRAYPAINT scoring (Level1 11 questions, Level2 11 questions, Level3 11 questions)
   */
  private void calculateSpraypaintScore(String skillSet) {
    weightedScore = 0.0;
    if (skillSet != null) {
      this.skillSet = skillSet;
      switch (skillSet) {
        case "level1" : 
          double[] weights1 = {10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 5.0, 5.0, 15.0, 5.0, 10.0};
          Integer[] answers1 = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11};
      
          for (int i = 0; i < answers1.length && i < weights1.length; i++) {
            if (answers1[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers1[i] / 5.0) * weights1[i];
            }
          }
          break;
        case "level2" :
           double[] weights2 = {10.0, 10.0, 5.0, 5.0, 10.0, 10.0, 10.0, 5.0, 10.0, 10.0, 15.0};
          Integer[] answers2 = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11};
      
          for (int i = 0; i < answers2.length && i < weights2.length; i++) {
            if (answers2[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers2[i] / 5.0) * weights2[i];
            }
          }
          break;
        case "level3" :
           double[] weights3 = {5.0, 10.0, 5.0, 5.0, 5.0, 5.0, 20.0, 5.0, 10.0, 15.0, 15.0};
          Integer[] answers3 = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11};
      
          for (int i = 0; i < answers3.length && i < weights3.length; i++) {
            if (answers3[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers3[i] / 5.0) * weights3[i];
            }
          }
        default : {
          // No additional calculation for undefined levels
        }
      }
    }
  }

  /**
   * METAL scoring (Level1 9 questions, Level2 10 questions, Level3 10 questions)
   */
  private void calculateMetalScore(String skillSet) {
    weightedScore = 0.0;
    if (skillSet != null) {
      this.skillSet = skillSet;
      switch (skillSet) {
        case "level1" : 
          double[] weights1 = {15.0, 15.0, 15.0, 15.0, 5.0, 5.0, 15.0, 5.0, 10.0};
          Integer[] answers1 = {q1, q2, q3, q4, q5, q6, q7, q8, q9};
      
          for (int i = 0; i < answers1.length && i < weights1.length; i++) {
            if (answers1[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers1[i] / 5.0) * weights1[i];
            }
          }
          break;
        case "level2" :
           double[] weights2 = {10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 5.0, 10.0, 10.0, 15.0};
          Integer[] answers2 = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10};
      
          for (int i = 0; i < answers2.length && i < weights2.length; i++) {
            if (answers2[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers2[i] / 5.0) * weights2[i];
            }
          }
          break;
        case "level3" :
           double[] weights3 = {10.0, 5.0, 5.0, 10.0, 5.0, 20.0, 5.0, 10.0, 15.0, 15.0};
          Integer[] answers3 = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10};
      
          for (int i = 0; i < answers3.length && i < weights3.length; i++) {
            if (answers3[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers3[i] / 5.0) * weights3[i];
            }
          }
        default : {
          // No additional calculation for undefined levels
        }
      }
    }
  }

  /**
   * METALCUT scoring (Level1 9 questions, Level2 11 questions, Level3 10 questions)
   */
  private void calculateMetalCutScore(String skillSet) {
    weightedScore = 0.0;
    if (skillSet != null) {
      this.skillSet = skillSet;
      switch (skillSet) {
        case "level1" : 
          double[] weights1 = {10.0, 20.0, 20.0, 10.0, 5.0, 5.0, 15.0, 5.0, 10.0};
          Integer[] answers1 = {q1, q2, q3, q4, q5, q6, q7, q8, q9};
      
          for (int i = 0; i < answers1.length && i < weights1.length; i++) {
            if (answers1[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers1[i] / 5.0) * weights1[i];
            }
          }
          break;
        case "level2" :
           double[] weights2 = {5.0, 10.0, 10.0, 5.0, 10.0, 10.0, 10.0, 5.0, 10.0, 10.0, 15.0};
          Integer[] answers2 = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11};
      
          for (int i = 0; i < answers2.length && i < weights2.length; i++) {
            if (answers2[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers2[i] / 5.0) * weights2[i];
            }
          }
          break;
        case "level3" :
           double[] weights3 = {10.0, 5.0, 5.0, 10.0, 5.0, 20.0, 5.0, 10.0, 15.0, 15.0};
          Integer[] answers3 = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10};
      
          for (int i = 0; i < answers3.length && i < weights3.length; i++) {
            if (answers3[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers3[i] / 5.0) * weights3[i];
            }
          }
        default : {
          // No additional calculation for undefined levels
        }
      }
    }
  }

  /**
   * M&E PROJECT scoring (Level1 10 questions, Level2 9 questions)
   */
  private void calculateMeProjectScore(String skillSet) {
    weightedScore = 0.0;
    if (skillSet != null) {
      this.skillSet = skillSet;
      switch (skillSet) {
        case "level1" : 
          double[] weights1 = {15.0, 10.0, 10.0, 10.0, 15.0, 5.0, 5.0, 15.0, 5.0,10.0};
          Integer[] answers1 = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10};
      
          for (int i = 0; i < answers1.length && i < weights1.length; i++) {
            if (answers1[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers1[i] / 5.0) * weights1[i];
            }
          }
          break;
        case "level2" :
           double[] weights2 = {15.0, 10.0, 15.0, 10.0, 10.0, 5.0, 10.0, 10.0, 15.0};
          Integer[] answers2 = {q1, q2, q3, q4, q5, q6, q7, q8, q9};
      
          for (int i = 0; i < answers2.length && i < weights2.length; i++) {
            if (answers2[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers2[i] / 5.0) * weights2[i];
            }
          }
          break;
        default : {
          // No additional calculation for undefined levels
        }
      }
    }
  }

  /**
   * TEAM-D scoring (Level1 10 questions, Level2 10 questions, Level3 10 questions, Level4 10 questions)
   */
  private void calculateTeamDScore(String skillSet) {
    weightedScore = 0.0;
    if (skillSet != null) {
      this.skillSet = skillSet;
      switch (skillSet) {
        case "level1" : 
          double[] weights1 = {15.0, 15.0, 10.0, 15.0, 5.0, 5.0, 5.0, 15.0, 5.0, 10.0};
          Integer[] answers1 = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10};
      
          for (int i = 0; i < answers1.length && i < weights1.length; i++) {
            if (answers1[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers1[i] / 5.0) * weights1[i];
            }
          }
          break;
        case "level2" :
           double[] weights2 = {10.0, 10.0, 10.0, 10.0, 10.0, 10.0, 5.0, 10.0, 10.0, 15.0};
          Integer[] answers2 = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10};
      
          for (int i = 0; i < answers2.length && i < weights2.length; i++) {
            if (answers2[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers2[i] / 5.0) * weights2[i];
            }
          }
          break;
        case "level3" :
           double[] weights3 = {10.0, 5.0, 5.0, 10.0, 5.0, 20.0, 5.0, 10.0, 15.0, 15.0};
          Integer[] answers3 = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10};
      
          for (int i = 0; i < answers3.length && i < weights3.length; i++) {
            if (answers3[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers3[i] / 5.0) * weights3[i];
            }
          }
          break;
        case "level4" :
           double[] weights4 = {5.0, 5.0, 5.0, 10.0, 10.0, 20.0, 5.0, 10.0, 15.0, 15.0};
          Integer[] answers4 = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10};
      
          for (int i = 0; i < answers4.length && i < weights4.length; i++) {
            if (answers4[i] != null) {
              // Convert rating (1-5) to percentage of weight
              weightedScore += (answers4[i] / 5.0) * weights4[i];
            }
          }
          break;
        default : {
          // No additional calculation for undefined levels
        }
      }
    }
  }

  /**
   * PM-EVALUATION scoring (6 questions)
   * Q1=30%, Q2=25%, Q3=20%, Q4=15%, Q5=5%, Q6=5%
   */
  private void calculatePmEvaluationScore() {
    double[] weights = {30.0, 25.0, 20.0, 15.0, 5.0, 5.0};
    Integer[] answers = {q1, q2, q3, q4, q5, q6};

    for (int i = 0; i < answers.length && i < weights.length; i++) {
      if (answers[i] != null) {
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

  public String getDuplicationSourceId() {
    return duplicationSourceId;
  }

  public void setDuplicationSourceId(String duplicationSourceId) {
    this.duplicationSourceId = duplicationSourceId;
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

  public Integer getQ12() {
    return q12;
  }

  public void setQ12(Integer q12) {
    this.q12 = q12;
  }

  public Integer getQ13() {
    return q13;
  }

  public void setQ13(Integer q13) {
    this.q13 = q13;
  }

  public Integer getQ14() {
    return q14;
  }

  public void setQ14(Integer q14) {
    this.q14 = q14;
  }

  public Integer getQ15() {
    return q15;
  }

  public void setQ15(Integer q15) {
    this.q15 = q15;
  }

  public Integer getQ16() {
    return q16;
  }

  public void setQ16(Integer q16) {
    this.q16 = q16;
  }

  public Integer getQ17() {
    return q17;
  }

  public void setQ17(Integer q17) {
    this.q17 = q17;
  }

  public Integer getQ18() {
    return q18;
  }

  public void setQ18(Integer q18) {
    this.q18 = q18;
  }

  public Integer getQ19() {
    return q19;
  }

  public void setQ19(Integer q19) {
    this.q19 = q19;
  }

  public Integer getQ20() {
    return q20;
  }

  public void setQ20(Integer q20) {
    this.q20 = q20;
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
