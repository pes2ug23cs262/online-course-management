package com.ocms.project.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificateId;

    private Long studentId;
    private Long courseId;
    private Long teacherId;
    private String certificateNumber;
    private Date issuanceDate;
    private String certificateUrl;
    private String status; // PENDING, APPROVED, REJECTED, ISSUED, REVOKED, EXPIRED
    private Double finalScore;
    
    // Approval fields
    private String approvalStatus; // PENDING, APPROVED, REJECTED
    private Long approvedBy; // Teacher ID who approved
    private Date approvalDate;
    private String approvalComments;
    private Date sentDate; // When certificate was sent to student

    // Constructors
    public Certificate() {}

    public Certificate(Long studentId, Long courseId, Long teacherId, Double finalScore) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.finalScore = finalScore;
        this.certificateNumber = "CERT-" + System.currentTimeMillis();
        this.issuanceDate = new Date();
        this.status = "PENDING";
        this.approvalStatus = "PENDING";
    }

    // Getters & Setters
    public Long getCertificateId() { return certificateId; }
    public void setCertificateId(Long certificateId) { this.certificateId = certificateId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }

    public Date getIssuanceDate() { return issuanceDate; }
    public void setIssuanceDate(Date issuanceDate) { this.issuanceDate = issuanceDate; }

    public String getCertificateUrl() { return certificateUrl; }
    public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getFinalScore() { return finalScore; }
    public void setFinalScore(Double finalScore) { this.finalScore = finalScore; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }

    public Date getApprovalDate() { return approvalDate; }
    public void setApprovalDate(Date approvalDate) { this.approvalDate = approvalDate; }

    public String getApprovalComments() { return approvalComments; }
    public void setApprovalComments(String approvalComments) { this.approvalComments = approvalComments; }

    public Date getSentDate() { return sentDate; }
    public void setSentDate(Date sentDate) { this.sentDate = sentDate; }
}