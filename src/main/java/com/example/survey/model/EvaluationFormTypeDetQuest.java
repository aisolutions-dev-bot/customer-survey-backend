package com.example.survey.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "m01EvaluationFormTypeDetQuest")
public class EvaluationFormTypeDetQuest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "UniqId")
  private Long uniqId;

  @Column(name = "FormType", length = 25)
  private String formType;

  @Column(name = "SkillSet", length = 25)
  private String skillSet;

  @Column(name = "SkillCategory", length = 45)
  private String skillCategory;

  @Column(name = "QuestionNumber")
  private Integer questionNumber;

  @Column(name = "Questionnaire", length = 255)
  private String questionnaire;

  @Column(name = "QuestionnaireZH", length = 255)
  private String questionnaireZh;

  @Column(name = "WeightedScore")
  private Double weightedScore;
}
