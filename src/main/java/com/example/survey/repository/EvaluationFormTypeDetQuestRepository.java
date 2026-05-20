package com.example.survey.repository;

import com.example.survey.model.EvaluationFormTypeDetQuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationFormTypeDetQuestRepository extends JpaRepository<EvaluationFormTypeDetQuest, Long> {

  List<EvaluationFormTypeDetQuest> findByFormTypeAndSkillSetOrderByQuestionNumber(
      String formType, String skillSet);

  @Query("SELECT DISTINCT e.skillSet FROM EvaluationFormTypeDetQuest e WHERE e.formType = ?1 ORDER BY e.skillSet")
  List<String> findDistinctSkillSetsByFormType(String formType);
}
