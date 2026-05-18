package com.example.survey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.survey.model.EvaluationRatingsDuplication;

@Repository
public interface EvaluationRatingsDuplicationRepository extends JpaRepository<EvaluationRatingsDuplication, Long> {

    List<EvaluationRatingsDuplication> findByEvaluationDistributionMgmtUniqId(Integer evaluationDistributionMgmtUniqId);
}
