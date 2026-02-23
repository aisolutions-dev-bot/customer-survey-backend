package com.example.survey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.survey.model.BSProjEvaluation;

@Repository
public interface BSProjEvaluationRepository extends JpaRepository<BSProjEvaluation, Long> {
    
    // Find evaluations by staff ID
    List<BSProjEvaluation> findByStaffId(String staffId);
    
    // Find evaluations by project ID
    List<BSProjEvaluation> findByProjectId(String projectId);
    
    // Find evaluations by evaluator ID
    List<BSProjEvaluation> findByEvaluatorId(String evaluatorId);
}