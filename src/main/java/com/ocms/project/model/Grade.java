package com.ocms.project.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gradeId;

    private Long studentId;
    private Long courseId;
    private Long submissionId; // Optional - grade can be for submission
    private Double marksObtained;
    private Double totalMarks;
    private String gradePoint; // A, B, C, D, F
    private String gradeStatus; // PASS, FAIL, INCOMPLETE
    private String feedback;
    private Date gradingDate;
    private Long gradedBy; // Teacher ID

    // Constructors
    public Grade() {}

    public Grade(Long studentId, Long courseId, Double marksObtained, Double totalMarks, Long gradedBy) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.marksObtained = marksObtained;
        this.totalMarks = totalMarks;
        this.gradedBy = gradedBy;
        this.gradingDate = new Date();
        this.gradeStatus = marksObtained >= (totalMarks * 0.4) ? "PASS" : "FAIL";
    }

    // Getters & Setters
    public Long getGradeId() { return gradeId; }
    public void setGradeId(Long gradeId) { this.gradeId = gradeId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getSubmissionId() { return submissionId; }
    public void setSubmissionId(Long submissionId) { this.submissionId = submissionId; }

    public Double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(Double marksObtained) { this.marksObtained = marksObtained; }

    public Double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Double totalMarks) { this.totalMarks = totalMarks; }

    public String getGradePoint() { return gradePoint; }
    public void setGradePoint(String gradePoint) { this.gradePoint = gradePoint; }

    public String getGradeStatus() { return gradeStatus; }
    public void setGradeStatus(String gradeStatus) { this.gradeStatus = gradeStatus; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public Date getGradingDate() { return gradingDate; }
    public void setGradingDate(Date gradingDate) { this.gradingDate = gradingDate; }

    public Long getGradedBy() { return gradedBy; }
    public void setGradedBy(Long gradedBy) { this.gradedBy = gradedBy; }
}