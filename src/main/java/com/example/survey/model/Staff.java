package com.example.survey.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "m03Staff")
public class Staff {
    
    @Id
    @Column(name = "StaffId", nullable=false)
    private String staffId;
    
    @Column(name = "Name", nullable=false)
    private String name;

    @Column(name = "TelMobile")
    private String telMobile;

    @Column(name = "EmailCompany")
    private String emailCompany;
    
    // Constructors
    public Staff() {
    }
    
    public Staff(String staffId, String name, String telMobile, String emailCompany) {
        this.staffId = staffId;
        this.name = name;
        this.telMobile = telMobile;
        this.emailCompany = emailCompany;
    }
    
    // Getters and Setters
    public String getStaffId() {
        return staffId;
    }
    
    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getTelMobile() { return telMobile; }    
    public void setTelMobile(String telMobile) { this.telMobile = telMobile; }
    
    public String getEmailCompany() { return emailCompany; }    
    public void setEmailCompany(String emailCompany) { this.emailCompany = emailCompany; }
    
    @Override
    public String toString() {
        return "Staff{" +
                "staffId='" + staffId + '\'' +
                ", name='" + name + '\'' +
                ", telMobile='" + telMobile + '\'' +
                ", emailCompany='" + emailCompany + '\'' +
                '}';
    }
}