package com.example.survey.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

  @Column(name = "ProjectId", length = 25)
  @JsonProperty("projectId")
  private String projectId;

  @Column(name = "DepartmentId")
  @JsonProperty("departmentId")
  private String departmentId;

  @Column(name = "EvaluatorId")
  @JsonProperty("evaluatorId")
  private String evaluatorId;

  @Column(name = "EvaluatorName")
  @JsonProperty("evaluatorName")
  private String evaluatorName;

  @Column(name = "SkillSet")
  @JsonProperty("skillSet")
  private String skillSet; // Values: 'junior', 'journeyman', 'senior'

  @Column(name = "FormType", length = 25)
  @JsonProperty("formType")
  private String formType;

  // NEW: Status field
  @Column(name = "Status")
  @JsonProperty("status")
  private String status; // Values: 'PENDING', 'SUBMITTED', 'COMPLETED', etc.

  @Column(name = "GroupId")
  @JsonProperty("groupId")
  private Integer groupId; // Values: 'PENDING', 'SUBMITTED', 'COMPLETED', etc.

  @Column(name = "SubmitDate")
  @JsonProperty("submitDate")
  private LocalDateTime submitDate;

}
