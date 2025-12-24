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
}
