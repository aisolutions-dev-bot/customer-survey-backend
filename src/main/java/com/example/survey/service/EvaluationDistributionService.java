package com.example.survey.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.survey.dto.EvaluationDistributionFullDTO;
import com.example.survey.model.EvaluationDistribution;
import com.example.survey.model.EvaluationDistributionNonProj;
import com.example.survey.repository.EvaluationDistributionRepository;
import com.example.survey.repository.EvaluationDistributionNonProjRepository;
import com.aisolutions.shared.util.DateUtil;

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
   * Create a SUBMITTED distribution row for a duplicated staff member,
   * copying metadata from the original distribution row.
   */
  public void createSubmittedForDuplicate(EvaluationDistribution original, String duplicateStaffId) {
    if (original == null) return;
    EvaluationDistribution dup = EvaluationDistribution.builder()
        .evaluateeId(duplicateStaffId)
        .projectId(original.getProjectId())
        .departmentId(original.getDepartmentId())
        .evaluatorId(original.getEvaluatorId())
        .evaluatorName(original.getEvaluatorName())
        .skillSet(original.getSkillSet())
        .formType(original.getFormType())
        .groupId(original.getGroupId())
        .status("SUBMITTED")
        .submitDate(DateUtil.nowSGT())
        .build();
    evaluationDistributionRepository.save(dup);
  }

  /**
   * Update the status of a project evaluation distribution row.
   * Does NOT touch the non-project table — use updateNonProjectStatus for that.
   *
   * @param uniqId The unique ID of the evaluation distribution
   * @param status The new status value (e.g., "SUBMITTED", "PENDING")
   * @return The updated EvaluationDistribution, or null if no project row matched
   */
  public EvaluationDistribution updateStatus(Integer uniqId, String status) {
    EvaluationDistribution distribution = evaluationDistributionRepository.findById(uniqId).orElse(null);
    if (distribution == null) {
      return null;
    }

    distribution.setStatus(status);
    if ("SUBMITTED".equalsIgnoreCase(status) && distribution.getSubmitDate() == null) {
      distribution.setSubmitDate(DateUtil.nowSGT());
    }

    return evaluationDistributionRepository.save(distribution);
  }

  /**
   * Update the status of a non-project evaluation distribution row.
   *
   * @param uniqId The unique ID of the evaluation distribution
   * @param status The new status value (e.g., "SUBMITTED", "PENDING")
   * @return The updated EvaluationDistributionNonProj, or null if no non-project row matched
   */
  public EvaluationDistributionNonProj updateNonProjectStatus(Integer uniqId, String status) {
    EvaluationDistributionNonProj nonProj = evaluationDistributionNonProjRepository.findById(uniqId).orElse(null);
    if (nonProj == null) {
      return null;
    }

    nonProj.setStatus(status);
    if ("SUBMITTED".equalsIgnoreCase(status) && nonProj.getSubmitDate() == null) {
      nonProj.setSubmitDate(DateUtil.nowSGT());
    }

    return evaluationDistributionNonProjRepository.save(nonProj);
  }
}
