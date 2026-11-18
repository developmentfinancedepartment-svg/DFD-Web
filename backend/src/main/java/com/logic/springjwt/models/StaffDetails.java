package com.logic.springjwt.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import java.time.LocalDateTime;

@Entity
@Table(name = "staff_details")
public class StaffDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "designation")
    private String designation;

    @Email
    @Column(name = "email")
    private String email;

    @Column(name = "linkedlin_link")
    private String linkedIn;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "status")
    private int status;

    @Column(name = "added_date")
    private LocalDateTime addedAt;

    @Column(name = "latest_Update")
    private LocalDateTime latestUpdate;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "staff_unit_id")
    @JsonBackReference
    private StaffUnits staffUnits;

    public StaffDetails() {
    }

    public StaffDetails(String name, String designation, String email, String linkedIn, String contactNumber, int status, LocalDateTime addedAt, LocalDateTime latestUpdate) {
        this.name = name;
        this.designation = designation;
        this.email = email;
        this.linkedIn = linkedIn;
        this.contactNumber = contactNumber;
        this.status = status;
        this.addedAt = addedAt;
        this.latestUpdate = latestUpdate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLinkedIn() {
        return linkedIn;
    }

    public void setLinkedIn(String linkedIn) {
        this.linkedIn = linkedIn;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public LocalDateTime getLatestUpdate() {
        return latestUpdate;
    }

    public void setLatestUpdate(LocalDateTime latestUpdate) {
        this.latestUpdate = latestUpdate;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public StaffUnits getStaffUnits() {
        return staffUnits;
    }

    public void setStaffUnits(StaffUnits staffUnits) {
        this.staffUnits = staffUnits;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
