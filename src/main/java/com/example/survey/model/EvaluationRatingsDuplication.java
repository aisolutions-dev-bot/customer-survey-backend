package com.example.survey.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "m17EvaluationRatingsDuplication")
public class EvaluationRatingsDuplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UniqId")
    private Long uniqId;

    @Column(name = "EvaluationDistributionMgmtUniqId", nullable = false)
    private Integer evaluationDistributionMgmtUniqId;

    @Column(name = "DuplicateStaffId", length = 25, nullable = false)
    private String duplicateStaffId;

    @Column(name = "EntryStaff", length = 25)
    private String entryStaff;

    @Column(name = "EntryDate")
    private LocalDateTime entryDate;

    public EvaluationRatingsDuplication() {
    }

    public Long getUniqId() {
        return uniqId;
    }

    public void setUniqId(Long uniqId) {
        this.uniqId = uniqId;
    }

    public Integer getEvaluationDistributionMgmtUniqId() {
        return evaluationDistributionMgmtUniqId;
    }

    public void setEvaluationDistributionMgmtUniqId(Integer evaluationDistributionMgmtUniqId) {
        this.evaluationDistributionMgmtUniqId = evaluationDistributionMgmtUniqId;
    }

    public String getDuplicateStaffId() {
        return duplicateStaffId;
    }

    public void setDuplicateStaffId(String duplicateStaffId) {
        this.duplicateStaffId = duplicateStaffId;
    }

    public String getEntryStaff() {
        return entryStaff;
    }

    public void setEntryStaff(String entryStaff) {
        this.entryStaff = entryStaff;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDateTime entryDate) {
        this.entryDate = entryDate;
    }
}
