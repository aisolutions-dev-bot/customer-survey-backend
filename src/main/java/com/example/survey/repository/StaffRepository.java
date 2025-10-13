package com.example.survey.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.survey.model.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, String> {
    
    // Find all staff ordered by name
    @Query(value = "SELECT * FROM m03Staff ORDER BY Name ASC", nativeQuery = true)
    List<Staff> findAllOrderByName();
    
    // Find staff by ID (inherited from JpaRepository, but you can add custom query if needed)
    @Query(value = "SELECT * FROM m03Staff WHERE StaffId = :staffId", nativeQuery = true)
    Optional<Staff> findByStaffId(@Param("staffId") String staffId);
}