package com.example.survey.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.survey.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {
    
    // Find all department ordered by name
    @Query(value = "SELECT * FROM m01Department ORDER BY DepartmentName ASC", nativeQuery = true)
    List<Department> findAllOrderByName();
    
    // Find department by ID (inherited from JpaRepository, but you can add custom query if needed)
    @Query(value = "SELECT * FROM m01Department WHERE DepartmentId = :departmentId", nativeQuery = true)
    Optional<Department> findByDepartmentId(@Param("departmentId") String departmentId);
}
