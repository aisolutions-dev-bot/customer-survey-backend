package com.example.survey.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
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
    
    // Default constructor
    public CarpentersEvaluation() {}
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    
    public String getRoleType() { return roleType; }
    public void setRoleType(String roleType) { this.roleType = roleType; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    
    public String getEvaluatorId() { return evaluatorId; }
    public void setEvaluatorId(String evaluatorId) { this.evaluatorId = evaluatorId; }
    
    public String getEvaluatorName() { return evaluatorName; }    
    public void setEvaluatorName(String evaluatorName) { this.evaluatorName = evaluatorName; }

    public String getCarpenterLevel() { return carpenterLevel; }
    public void setCarpenterLevel(String carpenterLevel) { this.carpenterLevel = carpenterLevel; }

    public Integer getQ1() { return q1; }
    public void setQ1(Integer q1) { this.q1 = q1;}
    
    public Integer getQ2() { return q2; }
    public void setQ2(Integer q2) { this.q2 = q2; }
    
    public Integer getQ3() { return q3; }
    public void setQ3(Integer q3) { this.q3 = q3; }
    
    public Integer getQ4() { return q4; }
    public void setQ4(Integer q4) { this.q4 = q4; }
    
    public Integer getQ5() { return q5; }
    public void setQ5(Integer q5) { this.q5 = q5; }

    public Integer getQ6() { return q6; }
    public void setQ6(Integer q6) { this.q6 = q6; }

    public Integer getQ7() { return q7; }
    public void setQ7(Integer q7) { this.q7 = q7; }

    public Integer getQ8() { return q8; }
    public void setQ8(Integer q8) { this.q8 = q8; }

    public Integer getQ9() { return q9; }
    public void setQ9(Integer q9) { this.q9 = q9; }

    public Integer getQ10() { return q10; } 
    public void setQ10(Integer q10) { this.q10 = q10; }

    public Integer getQ11() { return q11; }
    public void setQ11(Integer q11) { this.q11 = q11; }

    public Double getWeightedScore() { return weightedScore; }    
    public void setWeightedScore(Double weightedScore) { this.weightedScore = weightedScore; }
    
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        //calculateWeightedScore("");
    }
    
    private void calculateWeightedScore(String carpenterLevel) {
        // Weight percentages: q1=20%, q2=30%, q3=20%, q4=10%, q5=10%, q6=10%
        // Each question max is 5, so weighted_score = (q * weight/5)
        /* weightedScore = (q1 * 4.0) + (q2 * 6.0) + (q3 * 4.0) + 
                       (q4 * 2.0) + (q5 * 2.0) + (q6 * 2.0); */

        weightedScore = 0.0;// reset before calculation
        if (carpenterLevel!=null) {
            this.carpenterLevel = carpenterLevel;
            switch(this.carpenterLevel) {
                case "junior":
                    weightedScore += (q1 * 3.0) + (q2 * 3.0) + (q3 * 3.0) + 
                       (q4 * 3.0) + (q5 * 1.0) + (q6 * 1.0) + (q7 * 3.0) + 
                       (q8 * 1.0) + (q9 * 2.0);
                    break;
                case "journeyman":
                    weightedScore += (q1 * 2.0) + (q2 * 2.0) + (q3 * 2.0) + 
                       (q4 * 2.0) + (q5 * 1.0) + (q6 * 1.0) + (q7 * 2.0) +
                       (q8 * 1.0) + (q9 * 2.0) + (q10 * 2.0) + (q11 * 3.0);
                    break;
                case "senior":
                    weightedScore += (q1 * 2.0) + (q2 * 1.0) + (q3 * 1.0) + 
                       (q4 * 1.0) + (q5 * 1.0) + (q6 * 1.0) + (q7 * 4.0) +
                       (q8 * 1.0) + (q9 * 2.0) + (q10 * 3.0) + (q11 * 3.0);
                    break;
                default:
                    // No additional questions for undefined levels
                    break;
            }   
        }
    }
}