package com.ocms.project.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    private String reportType; // STUDENT_PERFORMANCE, COURSE_ANALYTICS, ENROLLMENT_STATS, PAYMENT_REPORT
    private Long generatedBy; // Admin ID
    private Date generatedDate;
    private String reportContent;
    private String filePath;
    private String status; // GENERATED, ARCHIVED
    private Long studentId; // Optional - only for student-specific reports
    private Long courseId; // Optional - only for course-specific reports

    // Constructors
    public Report() {}

    public Report(String reportType, Long generatedBy) {
        this.reportType = reportType;
        this.generatedBy = generatedBy;
        this.generatedDate = new Date();
        this.status = "GENERATED";
    }

    // Getters & Setters
    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public Long getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(Long generatedBy) { this.generatedBy = generatedBy; }

    public Date getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(Date generatedDate) { this.generatedDate = generatedDate; }

    public String getReportContent() { return reportContent; }
    public void setReportContent(String reportContent) { this.reportContent = reportContent; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
}