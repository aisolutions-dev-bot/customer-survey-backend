package com.example.survey.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "m12Project")
public class Project {
    
    @Id
    @Column(name = "ProjectCode", nullable = false)
    private String projectId;
    
    @Column(name = "ProjectName")
    private String projectName;
    
    @Column(name = "ClientName")
    private String clientName;
    
    @Column(name = "Status")
    private String status;
    
    // Constructors
    public Project() {
    }
    
    public Project(String projectId, String projectName, String clientName, String status) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.clientName = clientName;
        this.status = status;
    }
    
    // Getters and Setters
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Project{" +
                "projectId='" + projectId + '\'' +
                ", projectName='" + projectName + '\'' +
                ", clientName='" + clientName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}