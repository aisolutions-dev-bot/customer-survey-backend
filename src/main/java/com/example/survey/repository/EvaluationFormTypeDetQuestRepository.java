package com.example.survey.repository;

import com.example.survey.model.EvaluationFormTypeDetQuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationFormTypeDetQuestRepository extends JpaRepository<EvaluationFormTypeDetQuest, Long> {

  List<EvaluationFormTypeDetQuest> findByFormTypeAndSkillSetOrderByQuestionNumber(
      String formType, String skillSet);
}
