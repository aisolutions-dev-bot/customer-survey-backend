package com.example.survey.dto;

import com.example.survey.model.EvaluationFormTypeDetQuest;

public record FormQuestionDTO(
    Integer questionNumber,
    String questionnaire,
    String questionnaireZh,
    Double weightedScore,
    String skillCategory
) {
  public static FormQuestionDTO from(EvaluationFormTypeDetQuest q) {
    return new FormQuestionDTO(
        q.getQuestionNumber(),
        q.getQuestionnaire(),
        q.getQuestionnaireZh(),
        q.getWeightedScore(),
        q.getSkillCategory()
    );
  }
}
