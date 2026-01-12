package com.example.survey.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "m03Staff")
public class Staff {

  @Id
  @Column(name = "StaffId", nullable = false)
  private String staffId;

  @Column(name = "Name", nullable = false)
  private String name;

  @Column(name = "TelMobile")
  private String telMobile;

  @Column(name = "EmailCompany")
  private String emailCompany;

  @Column(name = "FormTypeId")
  private String formTypeId;
}
