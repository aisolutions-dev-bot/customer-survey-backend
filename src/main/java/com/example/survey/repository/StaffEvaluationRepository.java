package com.example.survey.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.survey.model.StaffEvaluation;

public interface StaffEvaluationRepository extends JpaRepository<StaffEvaluation, Long> {
}