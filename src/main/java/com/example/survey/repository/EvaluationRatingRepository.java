package com.example.survey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.survey.model.EvaluationRating;

@Repository
public interface EvaluationRatingRepository extends JpaRepository<EvaluationRating, Long> {
    
    // Find evaluations by evaluatee ID
    List<EvaluationRating> findByEvaluateeId(String evaluateeId);
    
    // Find evaluations by project code
    List<EvaluationRating> findByProjectCode(String projectCode);
    
    // Find evaluations by evaluator ID
    List<EvaluationRating> findByEvaluatorId(String evaluatorId);
    
    // Find evaluations by department ID
    List<EvaluationRating> findByDepartmentId(String departmentId);
    
    // Find evaluations by form type
    List<EvaluationRating> findByFormType(String formType);
    
    // Find evaluations by evaluatee and project
    List<EvaluationRating> findByEvaluateeIdAndProjectCode(String evaluateeId, String projectCode);
    
    // Find evaluations by project and form type
    List<EvaluationRating> findByProjectCodeAndFormType(String projectCode, String formType);
}
