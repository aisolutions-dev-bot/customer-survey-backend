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

import com.example.survey.model.Staff;
import com.example.survey.repository.StaffRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Adjust this to your Angular app URL in production
public class StaffController {
    
    @Autowired
    private StaffRepository staffRepository;
    
    /**
     * GET all staff ordered by name
     * Endpoint: GET /api/staff
     */
    @GetMapping("/staff")
    public ResponseEntity<List<Staff>> getAllStaff() {
        try {
            List<Staff> staffList = staffRepository.findAllOrderByName();
            
            if (staffList.isEmpty()) {
                // Return empty list instead of NO_CONTENT to avoid null issues in frontend
                return new ResponseEntity<>(staffList, HttpStatus.OK);
            }
            
            return new ResponseEntity<>(staffList, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error fetching staff list: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * GET staff by ID
     * Endpoint: GET /api/staff/{staffId}
     */
    @GetMapping("/staff/{staffId}")
    public ResponseEntity<Staff> getStaffById(@PathVariable String staffId) {
        try {
            Optional<Staff> staff = staffRepository.findByStaffId(staffId);
            
            if (staff.isPresent()) {
                return new ResponseEntity<>(staff.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error fetching staff by ID: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}