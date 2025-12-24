package com.example.survey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.survey.dto.EvaluationDistributionFullDTO;
import com.example.survey.model.EvaluationDistribution;

@Repository
public interface EvaluationDistributionRepository extends JpaRepository<EvaluationDistribution, Integer> {

  @Query(value = """
      SELECT
          e.UniqId,
          e.EvaluateeId,
          evalStaff.Name        AS evaluateeName,
          e.ProjectId,
          p.projectName         AS projectName,
          e.DepartmentId,
          d.departmentName      AS departmentName,
          e.EvaluatorId,
          evaluatorStaff.Name   AS evaluatorName,
          COALESCE(e.SkillSet, f.SkillSet) AS skillSet,
          e.Status,
          e.GroupId
      FROM m17EvaluationDistributionMgmt e
      LEFT JOIN m03Staff evalStaff
             ON e.EvaluateeId = evalStaff.staffId
      LEFT JOIN m03Staff evaluatorStaff
             ON e.EvaluatorId = evaluatorStaff.staffId
      LEFT JOIN m01Department d
             ON e.DepartmentId = d.departmentId
      LEFT JOIN m12Project p
             ON e.ProjectId = p.ProjectCode
      LEFT JOIN m01EvaluationFormType f
             ON f.UniqId = evalStaff.FormTypeId
      WHERE e.GroupId = :groupId
      AND e.status = 'PENDING'
      """, nativeQuery = true)
  List<EvaluationDistributionFullDTO> findPendingByGroupId(Integer groupId);

}
