package com.example.survey.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.survey.dto.EvaluationRatingRequest;
import com.example.survey.model.EvaluationRating;
import com.example.survey.repository.EvaluationRatingRepository;

@Service
public class EvaluationRatingService {

    @Autowired
    private EvaluationRatingRepository repository;

    /**
     * Save evaluation rating from request DTO
     */
    public EvaluationRating saveEvaluationRating(EvaluationRatingRequest request) {
        EvaluationRating rating = mapRequestToEntity(request);
        
        // Calculate weighted score if not provided
        if (rating.getWeightedScore() == null || rating.getWeightedScore() == 0.0) {
            rating.calculateWeightedScore();
        }
        
        return repository.save(rating);
    }

    /**
     * Map request DTO to entity
     */
    private EvaluationRating mapRequestToEntity(EvaluationRatingRequest request) {
        EvaluationRating rating = new EvaluationRating();
        
        // Map fields from request to entity
        rating.setEvaluateeId(request.getStaffId());
        rating.setProjectCode(request.getProjectId());
        rating.setDepartmentId(request.getDepartmentId());
        rating.setEvaluatorId(request.getEvaluatorId());
        rating.setFormType(request.getFormType());
        rating.setSkillSet(request.getCarpenterLevel());
        rating.setWeightedScore(request.getWeightedScore());
        rating.setRemarks(request.getRemarks());
        rating.setSubmittedAt(LocalDateTime.now());
        
        // Map question answers
        rating.setQ1(request.getQ1());
        rating.setQ2(request.getQ2());
        rating.setQ3(request.getQ3());
        rating.setQ4(request.getQ4());
        rating.setQ5(request.getQ5());
        rating.setQ6(request.getQ6());
        rating.setQ7(request.getQ7());
        rating.setQ8(request.getQ8());
        rating.setQ9(request.getQ9());
        rating.setQ10(request.getQ10());
        rating.setQ11(request.getQ11());
        rating.setQ12(request.getQ12());
        rating.setQ13(request.getQ13());
        rating.setQ14(request.getQ14());
        rating.setQ15(request.getQ15());
        rating.setQ16(request.getQ16());
        rating.setQ17(request.getQ17());
        rating.setQ18(request.getQ18());
        rating.setQ19(request.getQ19());
        rating.setQ20(request.getQ20());
        
        return rating;
    }

    /**
     * Get all evaluation ratings
     */
    public List<EvaluationRating> getAllRatings() {
        return repository.findAll();
    }

    /**
     * Get evaluations by evaluatee ID
     */
    public List<EvaluationRating> getRatingsByEvaluateeId(String evaluateeId) {
        return repository.findByEvaluateeId(evaluateeId);
    }

    /**
     * Get evaluations by project code
     */
    public List<EvaluationRating> getRatingsByProjectCode(String projectCode) {
        return repository.findByProjectCode(projectCode);
    }

    /**
     * Get evaluations by evaluator ID
     */
    public List<EvaluationRating> getRatingsByEvaluatorId(String evaluatorId) {
        return repository.findByEvaluatorId(evaluatorId);
    }

    /**
     * Get evaluations by department ID
     */
    public List<EvaluationRating> getRatingsByDepartmentId(String departmentId) {
        return repository.findByDepartmentId(departmentId);
    }

    /**
     * Get evaluations by form type
     */
    public List<EvaluationRating> getRatingsByFormType(String formType) {
        return repository.findByFormType(formType);
    }

    /**
     * Get evaluation by ID
     */
    public EvaluationRating getRatingById(Long uniqId) {
        return repository.findById(uniqId).orElse(null);
    }

    /**
     * Delete evaluation by ID
     */
    public boolean deleteRating(Long uniqId) {
        if (repository.existsById(uniqId)) {
            repository.deleteById(uniqId);
            return true;
        }
        return false;
    }
}
