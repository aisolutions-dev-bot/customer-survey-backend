package com.example.survey.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.survey.dto.EvaluationRatingRequest;
import com.example.survey.model.EvaluationFormTypeDetQuest;
import com.example.survey.model.EvaluationRating;
import com.example.survey.model.EvaluationRatingsDuplication;
import com.example.survey.repository.EvaluationFormTypeDetQuestRepository;
import com.example.survey.repository.EvaluationRatingRepository;
import com.example.survey.repository.EvaluationRatingsDuplicationRepository;

@Service
public class EvaluationRatingService {

    @Autowired
    private EvaluationRatingRepository repository;

    @Autowired
    private EvaluationRatingsDuplicationRepository duplicationRepository;

    @Autowired
    private EvaluationFormTypeDetQuestRepository formQuestRepository;

    /**
     * Save evaluation rating from request DTO
     */
    public EvaluationRating saveEvaluationRating(EvaluationRatingRequest request) {
        EvaluationRating rating = mapRequestToEntity(request);

        // Calculate weighted score from DB weights; fall back to hardcoded if not found
        if (rating.getWeightedScore() == null || rating.getWeightedScore() == 0.0) {
            List<EvaluationFormTypeDetQuest> questions = formQuestRepository
                .findByFormTypeAndSkillSetOrderByQuestionNumber(
                    rating.getFormType() != null ? rating.getFormType().toUpperCase() : "",
                    rating.getSkillSet() != null ? rating.getSkillSet().toUpperCase() : "");
            if (!questions.isEmpty()) {
                rating.setWeightedScore(calculateScoreFromDb(rating, questions));
            } else {
                rating.calculateWeightedScore();
            }
        }
        
        EvaluationRating savedRating = repository.save(rating);
        
        // Auto-create duplicate evaluation rating records
        if (request.getEvaluationDistributionMgmtUniqId() != null) {
            List<EvaluationRatingsDuplication> dupList =
                duplicationRepository.findByEvaluationDistributionMgmtUniqId(request.getEvaluationDistributionMgmtUniqId());
            
            for (EvaluationRatingsDuplication dup : dupList) {
                EvaluationRating dupRating = new EvaluationRating();
                dupRating.setEvaluateeId(dup.getDuplicateStaffId());
                dupRating.setProjectCode(savedRating.getProjectCode());
                dupRating.setDepartmentId(savedRating.getDepartmentId());
                dupRating.setEvaluatorId(savedRating.getEvaluatorId());
                dupRating.setFormType(savedRating.getFormType());
                dupRating.setSkillSet(savedRating.getSkillSet());
                dupRating.setWeightedScore(savedRating.getWeightedScore());
                dupRating.setRemarks(savedRating.getRemarks());
                dupRating.setSubmittedAt(savedRating.getSubmittedAt());
                dupRating.setQ1(savedRating.getQ1());
                dupRating.setQ2(savedRating.getQ2());
                dupRating.setQ3(savedRating.getQ3());
                dupRating.setQ4(savedRating.getQ4());
                dupRating.setQ5(savedRating.getQ5());
                dupRating.setQ6(savedRating.getQ6());
                dupRating.setQ7(savedRating.getQ7());
                dupRating.setQ8(savedRating.getQ8());
                dupRating.setQ9(savedRating.getQ9());
                dupRating.setQ10(savedRating.getQ10());
                dupRating.setQ11(savedRating.getQ11());
                dupRating.setQ12(savedRating.getQ12());
                dupRating.setQ13(savedRating.getQ13());
                dupRating.setQ14(savedRating.getQ14());
                dupRating.setQ15(savedRating.getQ15());
                dupRating.setQ16(savedRating.getQ16());
                dupRating.setQ17(savedRating.getQ17());
                dupRating.setQ18(savedRating.getQ18());
                dupRating.setQ19(savedRating.getQ19());
                dupRating.setQ20(savedRating.getQ20());
                repository.save(dupRating);
            }
        }
        
        return savedRating;
    }

    private double calculateScoreFromDb(EvaluationRating rating, List<EvaluationFormTypeDetQuest> questions) {
        Integer[] answers = {
            rating.getQ1(), rating.getQ2(), rating.getQ3(), rating.getQ4(), rating.getQ5(),
            rating.getQ6(), rating.getQ7(), rating.getQ8(), rating.getQ9(), rating.getQ10(),
            rating.getQ11(), rating.getQ12(), rating.getQ13(), rating.getQ14(), rating.getQ15(),
            rating.getQ16(), rating.getQ17(), rating.getQ18(), rating.getQ19(), rating.getQ20()
        };
        double rawScore = 0.0;
        double totalWeight = 0.0;
        for (EvaluationFormTypeDetQuest q : questions) {
            int idx = q.getQuestionNumber() - 1;
            if (idx >= 0 && idx < answers.length && answers[idx] != null && q.getWeightedScore() != null) {
                rawScore += answers[idx] * q.getWeightedScore();
                totalWeight += q.getWeightedScore();
            }
        }
        double maxScore = 5.0 * totalWeight;
        return maxScore > 0 ? Math.round((rawScore / maxScore) * 100.0) : 0.0;
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
