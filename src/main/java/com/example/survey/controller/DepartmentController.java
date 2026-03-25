package com.example.survey.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.survey.model.Department;
import com.example.survey.repository.DepartmentRepository;

@RestController
@RequestMapping("/api")
public class DepartmentController {

  @Autowired
  private DepartmentRepository departmentRepository;

  /**
   * GET all department ordered by name
   * Endpoint: GET /api/department
   */
  @GetMapping("/department")
  public ResponseEntity<List<Department>> getAllDepartment() {
    try {
      List<Department> departmentList = departmentRepository.findAllOrderByName();

      if (departmentList.isEmpty()) {
        // Return empty list instead of NO_CONTENT to avoid null issues in frontend
        return new ResponseEntity<>(departmentList, HttpStatus.OK);
      }

      return new ResponseEntity<>(departmentList, HttpStatus.OK);
    } catch (Exception e) {
      System.err.println("Error fetching department list: " + e.getMessage());
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * GET department by ID
   * Endpoint: GET /api/department/{departmentId}
   */
  @GetMapping("/department/{departmentId}")
  public ResponseEntity<Department> getDepartmentById(@PathVariable String departmentId) {
    try {
      Optional<Department> department = departmentRepository.findByDepartmentId(departmentId);

      if (department.isPresent()) {
        return new ResponseEntity<>(department.get(), HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      System.err.println("Error fetching department by ID: " + e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
