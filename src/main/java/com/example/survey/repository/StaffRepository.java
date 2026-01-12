package com.example.survey.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.survey.dto.StaffDTO;
import com.example.survey.model.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, String> {

  @Query(value = "SELECT * FROM m03Staff ORDER BY Name ASC", nativeQuery = true)
  List<Staff> findAllOrderByName();

  @Query(value = """
        SELECT
          s.StaffId       AS staffId,
          s.Name          AS name,
          f.FormType      AS formType,
          s.TelMobile     AS telMobile,
          s.EmailCompany  AS emailCompany
        FROM m03Staff s
        LEFT JOIN m01EvaluationFormType f
          ON s.FormTypeId = f.uniqId
        WHERE s.StaffId = :staffId
      """, nativeQuery = true)
  Optional<StaffDTO> findByStaffId(@Param("staffId") String staffId);
}
