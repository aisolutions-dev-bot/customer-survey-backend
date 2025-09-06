package com.example.survey.repository;

import com.example.survey.model.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
}