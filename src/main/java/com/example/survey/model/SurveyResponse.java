package com.example.survey.model;

import java.time.LocalDateTime;

import com.aisolutions.shared.util.DateUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
public class SurveyResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerId;

    @Column(length = 25)
    private String projectId;

    private Integer q1, q2, q3, q4, q5, q6, q7, q8, q9, q10;

    private LocalDateTime submittedAt = DateUtil.nowSGT();

    // Getters and setters omitted for brevity
    public long getId() { return id; };
    public void setId(long id) { this.id = id; };
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    // getters and setters for ALL fields (including q1..q10)
    public Integer getQ1() { return q1; }
    public void setQ1(Integer q1) { this.q1 = q1; }

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
    
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    @PrePersist
    @PreUpdate
    void validateProjectIdLength() {
        if (this.projectId != null && this.projectId.length() > 25) {
            throw new IllegalArgumentException(
                "Project Code exceeds maximum length of 25 characters (got " + this.projectId.length() + ")"
            );
        }
    }
}