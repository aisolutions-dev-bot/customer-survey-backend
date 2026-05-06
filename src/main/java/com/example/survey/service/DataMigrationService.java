package com.example.survey.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.survey.model.EvaluationRating;
import com.example.survey.repository.EvaluationDistributionRepository;
import com.example.survey.repository.EvaluationRatingRepository;

@Service
public class DataMigrationService implements CommandLineRunner {
    
    @Autowired
    private EvaluationRatingRepository repository;
    
    @Autowired
    private EvaluationDistributionRepository distRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Value("${migration.recalculate-carpenter-scores:false}")
    private boolean recalculateScores;
    
    @Value("${migration.populate-carpenter-formtype:false}")
    private boolean populateFormType;
    
    @Override
    public void run(String... args) {
        if (recalculateScores) {
            recalculateCarpenterScores();
        }
        if (populateFormType) {
            populateCarpenterFormType();
        }
    }
    
    /**
     * Recalculate scores for all existing CARPENTER form type entries.
     * Uses the updated calculateWeightedScore() method which delegates to
     * calculateCarpenterScore() for CARPENTER form type.
     */
    public int recalculateCarpenterScores() {
        List<EvaluationRating> carpenterRatings = repository.findByFormType("CARPENTER");
        int count = 0;
        for (EvaluationRating rating : carpenterRatings) {
            rating.calculateWeightedScore();
            repository.save(rating);
            System.out.println("Recalculated score for UniqId=" + rating.getUniqId() + ", new score=" + rating.getWeightedScore());
            count++;
        }
        System.out.println("Total scores recalculated: " + count);
        return count;
    }
    
    /**
     * Populate FormType in distribution table for carpenter entries.
     * Updates rows where FormType is NULL and SkillSet indicates carpenter level.
     */
    public int populateCarpenterFormType() {
        String sql = "UPDATE m17EvaluationDistributionMgmt SET FormType = 'CARPENTER' WHERE FormType IS NULL AND SkillSet IN ('junior', 'journeyman', 'senior')";
        int updated = jdbcTemplate.update(sql);
        System.out.println("Populated FormType for " + updated + " distribution rows");
        return updated;
    }
}
