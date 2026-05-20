package com.example.survey.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.survey.dto.EvaluationDistributionFullDTO;
import com.example.survey.model.EvaluationDistribution;
import com.example.survey.model.EvaluationDistributionNonProj;
import com.example.survey.repository.EvaluationDistributionRepository;
import com.example.survey.repository.EvaluationDistributionNonProjRepository;
import com.example.survey.util.DateUtil;

@Service
public class EvaluationDistributionService {

  @Autowired
  private EvaluationDistributionRepository evaluationDistributionRepository;

  @Autowired
  private EvaluationDistributionNonProjRepository evaluationDistributionNonProjRepository;

  /**
   * Get evaluation distribution by uniqId from m17EvaluationDistributionMgmt
   * table
   */
  public EvaluationDistribution getByUniqId(Integer uniqId) {
    return evaluationDistributionRepository.findById(uniqId).orElse(null);
  }

  /**
   * Get pending evaluation distribution by groupId.
   * Tries m17EvaluationDistributionMgmt first, falls back to m17EvaluationDistNonProj.
   */
  public List<EvaluationDistributionFullDTO> getPendingByUniqId(Integer groupId) {
    List<EvaluationDistributionFullDTO> result = evaluationDistributionRepository.findPendingByGroupId(groupId);
    if (result == null || result.isEmpty()) {
      result = evaluationDistributionNonProjRepository.findPendingByGroupId(groupId);
    }
    return result;
  }

  /**
   * Update the status of an evaluation distribution
   * 
   * @param uniqId The unique ID of the evaluation distribution
   * @param status The new status value (e.g., "SUBMITTED", "PENDING",
   *               "COMPLETED")
   * @return The updated EvaluationDistribution or null if not found
   */
  public EvaluationDistribution updateStatus(Integer uniqId, String status) {
    EvaluationDistribution distribution = evaluationDistributionRepository.findById(uniqId).orElse(null);

    if (distribution != null) {
      distribution.setStatus(status);

      if ("SUBMITTED".equalsIgnoreCase(status) && distribution.getSubmitDate() == null) {
        distribution.setSubmitDate(DateUtil.nowSGT());
      }

      return evaluationDistributionRepository.save(distribution);
    }

    // Fall back to non-project table
    EvaluationDistributionNonProj nonProj = evaluationDistributionNonProjRepository.findById(uniqId).orElse(null);

    if (nonProj != null) {
      nonProj.setStatus(status);

      if ("SUBMITTED".equalsIgnoreCase(status) && nonProj.getSubmitDate() == null) {
        nonProj.setSubmitDate(DateUtil.nowSGT());
      }

      evaluationDistributionNonProjRepository.save(nonProj);
    }

    return null;
  }
}
