package com.ocms.project.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    private Long courseId;
    private Long teacherId;
    private String title;
    private String description;
    private Date creationDate;
    private Date deadline;
    private Double totalMarks;
    private String status; // ACTIVE, CLOSED, ARCHIVED

    // Constructors
    public Assignment() {}

    public Assignment(Long courseId, Long teacherId, String title, String description, Date deadline, Double totalMarks) {
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.totalMarks = totalMarks;
        this.creationDate = new Date();
        this.status = "ACTIVE";
    }

    // Getters & Setters
    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }

    public Double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Double totalMarks) { this.totalMarks = totalMarks; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}