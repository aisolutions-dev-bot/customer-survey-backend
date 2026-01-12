package com.example.survey.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "m01EvaluationFormType")
public class EvaluationFormType {

  @Id
  @Column(name = "UniqId", nullable = false)
  private Long uniqId;

  @Column(name = "FormType", nullable = true)
  private String formType;

  @Column(name = "SkillSet", nullable = true)
  private String skillSet;

  @Column(name = "EntryStaff", nullable = true)
  private String entryStaff;

  @Column(name = "EntryDate", nullable = true)
  private java.util.Date entryDate;

  @Column(name = "LastEditStaff", nullable = true)
  private String lastEditStaff;

  @Column(name = "LastEditDate", nullable = true)
  private java.util.Date lastEditDate;
}
