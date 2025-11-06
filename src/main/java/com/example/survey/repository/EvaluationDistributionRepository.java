package com.example.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.survey.model.EvaluationDistribution;

@Repository
public interface  EvaluationDistributionRepository extends JpaRepository<EvaluationDistribution, Integer> {
    // JpaRepository provides findById() method automatically
    // Additional custom query methods can be added here if needed
}
