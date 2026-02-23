package com.example.survey.dto;

public class EvaluationRatingRequest {
    
    private String staffId;           // Maps to EvaluateeId
    private String projectId;         // Maps to ProjectCode
    private String projectName;       // Not stored in m17EvaluationRatings
    private String departmentId;      // Maps to DepartmentId
    private String evaluatorId;       // Maps to EvaluatorId
    private String evaluatorName;     // Not stored in m17EvaluationRatings
    private String formType;          // Maps to FormType
    private String carpenterLevel;    // Maps to SkillSet
    private Double weightedScore;     // Maps to WeightedScore
    private String remarks;           // Maps to Remarks
    
    // Question answers
    private Integer q1;
    private Integer q2;
    private Integer q3;
    private Integer q4;
    private Integer q5;
    private Integer q6;
    private Integer q7;
    private Integer q8;
    private Integer q9;
    private Integer q10;
    private Integer q11;
    
    // Constructors
    public EvaluationRatingRequest() {
    }

    public EvaluationRatingRequest(String staffId, String projectId, String projectName,
                                   String departmentId, String evaluatorId, String evaluatorName,
                                   String formType, String carpenterLevel, Double weightedScore,
                                   String remarks, Integer q1, Integer q2, Integer q3, Integer q4,
                                   Integer q5, Integer q6, Integer q7, Integer q8, Integer q9,
                                   Integer q10, Integer q11) {
        this.staffId = staffId;
        this.projectId = projectId;
        this.projectName = projectName;
        this.departmentId = departmentId;
        this.evaluatorId = evaluatorId;
        this.evaluatorName = evaluatorName;
        this.formType = formType;
        this.carpenterLevel = carpenterLevel;
        this.weightedScore = weightedScore;
        this.remarks = remarks;
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
        this.q4 = q4;
        this.q5 = q5;
        this.q6 = q6;
        this.q7 = q7;
        this.q8 = q8;
        this.q9 = q9;
        this.q10 = q10;
        this.q11 = q11;
    }

    // Getters and Setters
    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
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

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getCarpenterLevel() {
        return carpenterLevel;
    }

    public void setCarpenterLevel(String carpenterLevel) {
        this.carpenterLevel = carpenterLevel;
    }

    public Double getWeightedScore() {
        return weightedScore;
    }

    public void setWeightedScore(Double weightedScore) {
        this.weightedScore = weightedScore;
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
        return "EvaluationRatingRequest{" +
                "staffId='" + staffId + '\'' +
                ", projectId='" + projectId + '\'' +
                ", projectName='" + projectName + '\'' +
                ", departmentId='" + departmentId + '\'' +
                ", evaluatorId='" + evaluatorId + '\'' +
                ", evaluatorName='" + evaluatorName + '\'' +
                ", formType='" + formType + '\'' +
                ", carpenterLevel='" + carpenterLevel + '\'' +
                ", weightedScore=" + weightedScore +
                ", q1=" + q1 + ", q2=" + q2 + ", q3=" + q3 +
                ", q4=" + q4 + ", q5=" + q5 + ", q6=" + q6 +
                ", q7=" + q7 +
                '}';
    }
}
