package com.example.survey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.survey.model.CarpentersEvaluation;

@Repository
public interface CarpentersEvaluationRepository extends JpaRepository<CarpentersEvaluation, Long> {
    
    // Find evaluations by staff ID
    List<CarpentersEvaluation> findByStaffId(String staffId);
    
    // Find evaluations by project ID
    List<CarpentersEvaluation> findByProjectId(String projectId);
    
    // Find evaluations by evaluator ID
    List<CarpentersEvaluation> findByEvaluatorId(String evaluatorId);
}