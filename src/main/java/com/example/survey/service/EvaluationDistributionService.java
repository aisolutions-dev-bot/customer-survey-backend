package com.example.survey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.survey.model.EvaluationDistribution;
import com.example.survey.repository.EvaluationDistributionRepository;

@Service
public class EvaluationDistributionService {
    
    @Autowired
    private EvaluationDistributionRepository evaluationDistributionRepository;

    /**
     * Get evaluation distribution by uniqId from m17EvaluationDistributionMgmt table
     */
    public EvaluationDistribution getByUniqId(Integer uniqId) {
        return evaluationDistributionRepository.findById(uniqId).orElse(null);
    }
    
    /**
     * Update the status of an evaluation distribution
     * @param uniqId The unique ID of the evaluation distribution
     * @param status The new status value (e.g., "SUBMITTED", "PENDING", "COMPLETED")
     * @return The updated EvaluationDistribution or null if not found
     */
    public EvaluationDistribution updateStatus(Integer uniqId, String status) {
        EvaluationDistribution distribution = evaluationDistributionRepository.findById(uniqId).orElse(null);
        
        if (distribution != null) {
            distribution.setStatus(status);
            return evaluationDistributionRepository.save(distribution);
        }
        
        return null;
    }    
}
