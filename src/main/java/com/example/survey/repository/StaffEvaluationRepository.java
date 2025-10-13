package com.example.survey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.survey.model.StaffEvaluation;

@Repository
public interface StaffEvaluationRepository extends JpaRepository<StaffEvaluation, Long> {
    
    // Find evaluations by staff ID
    List<StaffEvaluation> findByStaffId(String staffId);
    
    // Find evaluations by project ID
    List<StaffEvaluation> findByProjectId(String projectId);
    
    // Find evaluations by evaluator ID
    List<StaffEvaluation> findByEvaluatorId(String evaluatorId);
}