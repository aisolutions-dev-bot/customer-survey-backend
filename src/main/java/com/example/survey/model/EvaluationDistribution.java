package com.example.survey.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "m17EvaluationDistributionMgmt")
public class EvaluationDistribution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UniqId")
    @JsonProperty("uniqId")
    private Integer uniqId;

    @Column(name = "EvaluateeId")
    @JsonProperty("evaluateeId")
    private String evaluateeId;

    @Column(name = "ProjectId")
    @JsonProperty("projectId")
    private String projectId;

    @Column(name = "ProjectName")
    @JsonProperty("projectName")
    private String projectName;

    @Column(name = "DepartmentId")
    @JsonProperty("departmentId")
    private String departmentId;

    @Column(name = "DepartmentName")
    @JsonProperty("departmentName")
    private String departmentName;

    @Column(name = "EvaluatorId")
    @JsonProperty("evaluatorId")
    private String evaluatorId;

    @Column(name = "EvaluatorName")
    @JsonProperty("evaluatorName")
    private String evaluatorName;

    @Column(name = "SkillSet")
    @JsonProperty("skillSet")
    private String skillSet; // Values: 'junior', 'journeyman', 'senior'

    // NEW: Status field
    @Column(name = "Status")
    @JsonProperty("status")
    private String status; // Values: 'PENDING', 'SUBMITTED', 'COMPLETED', etc.

    // Constructors
    public EvaluationDistribution() {
    }

    public EvaluationDistribution(Integer uniqId, String evaluateeId, String projectId, 
                                 String projectName, String departmentId, String departmentName,
                                 String evaluatorId, String evaluatorName, String skillSet, String status) {
        this.uniqId = uniqId;
        this.evaluateeId = evaluateeId;
        this.projectId = projectId;
        this.projectName = projectName;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.evaluatorId = evaluatorId;
        this.evaluatorName = evaluatorName;
        this.skillSet = skillSet;
        this.status = status;
    }

    // Getters and Setters
    public Integer getUniqId() {
        return uniqId;
    }

    public void setUniqId(Integer uniqId) {
        this.uniqId = uniqId;
    }

    public String getEvaluateeId() {
        return evaluateeId;
    }

    public void setEvaluateeId(String evaluateeId) {
        this.evaluateeId = evaluateeId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getEvaluatorId() {
        return evaluatorId;
    }

    public void setEvaluatorId(String evaluatorId) {
        this.evaluatorId = evaluatorId;
    }

    public String getEvaluatorName() {
        return evaluatorName;
    }

    public void setEvaluatorName(String evaluatorName) {
        this.evaluatorName = evaluatorName;
    }

    public String getSkillSet() {
        return skillSet;
    }

    public void setSkillSet(String skillSet) {
        this.skillSet = skillSet;
    }

    // NEW: Status getter and setter
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "EvaluationDistribution{" +
                "uniqId=" + uniqId +
                ", evaluateeId='" + evaluateeId + '\'' +
                ", projectId='" + projectId + '\'' +
                ", projectName='" + projectName + '\'' +
                ", departmentId='" + departmentId + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", evaluatorId='" + evaluatorId + '\'' +
                ", evaluatorName='" + evaluatorName + '\'' +
                ", skillSet='" + skillSet + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
