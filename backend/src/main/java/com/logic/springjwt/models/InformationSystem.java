package com.logic.springjwt.models;

import jakarta.persistence.*;

@Entity
@Table(name = "information_system")
public class InformationSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ministry")
    private String ministry;

    @Column(name = "institute")
    private String institute;

    @Column(name = "system_name")
    private String systemName;

    @Column(name = "scope", columnDefinition = "TEXT")
    private String scope;

    @Column(name = "weblink" , columnDefinition = "TEXT")
    private String weblink;

    @Column(name = "contact_person" , columnDefinition = "TEXT")
    private String contactPerson;

    @Column(name = "contact_role_person" , columnDefinition = "TEXT")
    private String contact1Designation;

    @Column(name = "contact_email_person" , columnDefinition = "TEXT")
    private String contact1Email;

    @Column(name = "phone_office_person" , columnDefinition = "TEXT")
    private String contact1OfficialNumber;

    @Column(name = "phone_mobile_person" , columnDefinition = "TEXT")
    private String contact1PersonalNumber;

//    @Column(name = "contact_role_person_2")
//    private String contact2Designation;
//
//    @Column(name = "contact_email_person_2")
//    private String contact2Email;
//
//    @Column(name = "phone_office_person_2")
//    private String contact2OfficialNumber;
//
//    @Column(name = "phone_mobile_person_2")
//    private String contact2PersonalNumber;

    // ===== GETTERS & SETTERS =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMinistry() { return ministry; }
    public void setMinistry(String ministry) { this.ministry = ministry; }

    public String getInstitute() { return institute; }
    public void setInstitute(String institute) { this.institute = institute; }

    public String getSystemName() { return systemName; }
    public void setSystemName(String systemName) { this.systemName = systemName; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getWeblink() { return weblink; }
    public void setWeblink(String weblink) { this.weblink = weblink; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContact1Designation() { return contact1Designation; }
    public void setContact1Designation(String contact1Designation) { this.contact1Designation = contact1Designation; }

    public String getContact1Email() { return contact1Email; }
    public void setContact1Email(String contact1Email) { this.contact1Email = contact1Email; }

    public String getContact1OfficialNumber() { return contact1OfficialNumber; }
    public void setContact1OfficialNumber(String contact1OfficialNumber) { this.contact1OfficialNumber = contact1OfficialNumber; }

    public String getContact1PersonalNumber() { return contact1PersonalNumber; }
    public void setContact1PersonalNumber(String contact1PersonalNumber) { this.contact1PersonalNumber = contact1PersonalNumber; }

//    public String getContact2Designation() { return contact2Designation; }
//    public void setContact2Designation(String contact2Designation) { this.contact2Designation = contact2Designation; }
//
//    public String getContact2Email() { return contact2Email; }
//    public void setContact2Email(String contact2Email) { this.contact2Email = contact2Email; }
//
//    public String getContact2OfficialNumber() { return contact2OfficialNumber; }
//    public void setContact2OfficialNumber(String contact2OfficialNumber) { this.contact2OfficialNumber = contact2OfficialNumber; }
//
//    public String getContact2PersonalNumber() { return contact2PersonalNumber; }
//    public void setContact2PersonalNumber(String contact2PersonalNumber) { this.contact2PersonalNumber = contact2PersonalNumber; }
}
