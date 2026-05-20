package com.example.survey.model;

import java.time.LocalDateTime;

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
@Table(name = "m17EvaluationDistNonProj")
public class EvaluationDistributionNonProj {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "UniqId")
  private Integer uniqId;

  @Column(name = "GroupId")
  private Integer groupId;

  @Column(name = "EvaluateeId")
  private String evaluateeId;

  @Column(name = "DepartmentId")
  private String departmentId;

  @Column(name = "ProjectId")
  private String projectId;

  @Column(name = "EvaluatorId")
  private String evaluatorId;

  @Column(name = "EvaluatorName")
  private String evaluatorName;

  @Column(name = "SkillSet")
  private String skillSet;

  @Column(name = "FormType")
  private String formType;

  @Column(name = "Status")
  private String status;

  @Column(name = "SubmitDate")
  private LocalDateTime submitDate;
}
