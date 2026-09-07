package com.example.survey.dto;

public interface EvaluationDistributionFullDTO {
  Integer getUniqId();

  String getEvaluateeId();

  String getEvaluateeName(); // <- coming from join

  String getProjectId();

  String getProjectName();

  String getDepartmentName(); // <- coming from join

  String getDepartmentId();

  String getEvaluatorId();

  String getEvaluatorName(); // <- coming from join

  String getSkillSet();

  String getStatus();

  Integer getGroupId();

  String getFormType();

  String getDistributionType(); // "PROJECT" or "NON_PROJECT" - which distribution table this row came from

  String getLinkProjId(); // Linked project code (Non-Project distributions only); null for Project distributions
}
