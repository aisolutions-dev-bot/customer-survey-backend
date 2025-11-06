package com.example.survey.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "m01Department")
public class Department {
    
    @Id
    @Column(name = "DepartmentId", nullable=false)
    private String departmentId;
    
    @Column(name = "DepartmentName", nullable=false)
    private String departmentName;

    // Constructors
    public Department() {
    }
    
    public Department(String departmentId, String departmentName) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }
    
    // Getters and Setters
    public String getDepartmentId() {
        return departmentId;
    }
    
    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }
    
    public String getDepartmentName() {
        return departmentName;
    }
    
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    
    @Override
    public String toString() {
        return "Department{" +
                "DepartmentId='" + departmentId + '\'' +
                ", DepartmentName='" + departmentName + '\'' +
                '}';
    }
}
