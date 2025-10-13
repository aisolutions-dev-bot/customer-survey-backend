package com.example.survey.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.survey.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    
    // Find all projects ordered by project name (or use native query if needed)
    @Query(value = "SELECT * FROM m12Project ORDER BY ProjectName ASC", nativeQuery = true)
    List<Project> findAllOrderByProjectName();
    
    // Find project by ID
    @Query(value = "SELECT * FROM m12Project WHERE ProjectCode = :projectId", nativeQuery = true)
    Optional<Project> findByProjectId(@Param("projectId") String projectId);
    
    // Optional: Find only active projects
    @Query(value = "SELECT * FROM m12Project WHERE Status = 'Active' ORDER BY ProjectName ASC", nativeQuery = true)
    List<Project> findActiveProjectsOrderByProjectName();
}