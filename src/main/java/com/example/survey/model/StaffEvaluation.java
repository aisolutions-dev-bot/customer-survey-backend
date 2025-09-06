package com.example.survey.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class StaffEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String staffId;
    private String projectId;
    private String roleType; // "drafter", "engineer", etc.
    
    // Questions with weighted scores
    private Integer q1; // workProgressManagement 20%
    private Integer q2; // accuracy 30%
    private Integer q3; // jobKnowledgeSkills 20%
    private Integer q4; // teamwork 10%
    private Integer q5; // dependabilityInitiative 10%
    private Integer q6; // communicationResponsiveness 10%
    
    private LocalDateTime submittedAt = LocalDateTime.now();
    
    // Default constructor
    public StaffEvaluation() {}
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
    
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    
    public String getRoleType() { return roleType; }
    public void setRoleType(String roleType) { this.roleType = roleType; }
    
    public Integer getQ1() { return q1; }
    public void setQ1(Integer q1) {this.q1 = q1;}
    
    public Integer getQ2() { return q2; }
    public void setQ2(Integer q2) { this.q2 = q2; }
    
    public Integer getQ3() { return q3; }
    public void setQ3(Integer q3) {this.q3 = q3; }
    
    public Integer getQ4() { return q4; }
    public void setQ4(Integer q4) {this.q4 = q4; }
    
    public Integer getQ5() { return q5; }
    public void setQ5(Integer q5) {this.q5 = q5; }

    public Integer getQ6() { return q6; }
    public void setQ6(Integer q6) {this.q6 = q6; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}