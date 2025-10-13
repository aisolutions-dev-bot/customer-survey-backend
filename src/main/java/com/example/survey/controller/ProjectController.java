package com.example.survey.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.survey.model.Project;
import com.example.survey.repository.ProjectRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Adjust this to your Angular app URL in production
public class ProjectController {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    /**
     * GET all projects ordered by project name
     * Endpoint: GET /api/projects
     */
    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getAllProjects() {
        try {
            List<Project> projectList = projectRepository.findAllOrderByProjectName();
            
            if (projectList.isEmpty()) {
                // Return empty list instead of NO_CONTENT to avoid null issues in frontend
                return new ResponseEntity<>(projectList, HttpStatus.OK);
            }
            
            return new ResponseEntity<>(projectList, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error fetching project list: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * GET active projects only
     * Endpoint: GET /api/projects/active
     */
    @GetMapping("/projects/active")
    public ResponseEntity<List<Project>> getActiveProjects() {
        try {
            List<Project> projectList = projectRepository.findActiveProjectsOrderByProjectName();
            
            if (projectList.isEmpty()) {
                return new ResponseEntity<>(projectList, HttpStatus.OK);
            }
            
            return new ResponseEntity<>(projectList, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error fetching active project list: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * GET project by ID
     * Endpoint: GET /api/projects/{projectId}
     */
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable String projectId) {
        try {
            Optional<Project> project = projectRepository.findByProjectId(projectId);
            
            if (project.isPresent()) {
                return new ResponseEntity<>(project.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error fetching project by ID: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}