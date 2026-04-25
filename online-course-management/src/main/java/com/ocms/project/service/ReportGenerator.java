package com.ocms.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocms.project.model.Assignment;
import com.ocms.project.model.Certificate;
import com.ocms.project.model.EnrollmentRecord;
import com.ocms.project.model.Grade;
import com.ocms.project.model.Report;
import com.ocms.project.model.Submission;
import com.ocms.project.repository.CertificateRepository;
import com.ocms.project.repository.EnrollmentRepository;
import com.ocms.project.repository.GradeRepository;
import com.ocms.project.repository.ReportRepository;
import com.ocms.project.repository.UserRepository;
import com.ocms.project.repository.SubmissionRepository;
import com.ocms.project.repository.AssignmentRepository;

/**
 * ReportGenerator - Admin Report Generation Service
 * Uses Template Method Pattern for different report types
 */
@Service
public class ReportGenerator {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    public Report generateStudentPerformanceReport(Long adminId, Long studentId) {
        Report report = new Report("STUDENT_PERFORMANCE", adminId);
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        
        StringBuilder content = new StringBuilder();
        content.append("STUDENT PERFORMANCE REPORT\n");
        content.append("==========================\n");
        content.append("Student ID: ").append(studentId).append("\n");
        content.append("Total Courses: ").append(grades.size()).append("\n");
        
        double avgMarks = grades.stream().mapToDouble(Grade::getMarksObtained).average().orElse(0);
        content.append("Average Marks: ").append(String.format("%.2f", avgMarks)).append("\n");
        
        long passCount = grades.stream().filter(g -> "PASS".equals(g.getGradeStatus())).count();
        double passRate = grades.isEmpty() ? 0.0 : (passCount * 100.0 / grades.size());
        content.append("Pass Rate: ").append(String.format("%.2f%%", passRate)).append("\n");
        
        report.setReportContent(content.toString());
        report.setStudentId(studentId);
        return reportRepository.save(report);
    }

    public Report generateCourseAnalyticsReport(Long adminId, Long courseId) {
        Report report = new Report("COURSE_ANALYTICS", adminId);
        List<Grade> grades = gradeRepository.findByCourseId(courseId);
        
        StringBuilder content = new StringBuilder();
        content.append("COURSE ANALYTICS REPORT\n");
        content.append("=======================\n");
        content.append("Course ID: ").append(courseId).append("\n");
        content.append("Total Students: ").append(grades.size()).append("\n");
        
        long passCount = grades.stream().filter(g -> "PASS".equals(g.getGradeStatus())).count();
        double passRate = grades.isEmpty() ? 0.0 : (passCount * 100.0 / grades.size());
        content.append("Pass Rate: ").append(String.format("%.2f%%", passRate)).append("\n");
        
        double avgMarks = grades.stream().mapToDouble(Grade::getMarksObtained).average().orElse(0);
        content.append("Average Marks: ").append(String.format("%.2f", avgMarks)).append("\n");
        
        long failCount = grades.stream().filter(g -> "FAIL".equals(g.getGradeStatus())).count();
        content.append("Fail Count: ").append(failCount).append("\n");
        
        report.setReportContent(content.toString());
        report.setCourseId(courseId);
        return reportRepository.save(report);
    }

    // Generate detailed course report including enrollments and certificates
    public Report generateDetailedCourseReport(Long adminId, Long courseId) {
        Report report = new Report("DETAILED_COURSE_REPORT", adminId);
        
        List<EnrollmentRecord> enrollments = enrollmentRepository.findByCourseId(courseId);
        List<Grade> grades = gradeRepository.findByCourseId(courseId);
        List<Certificate> certificates = certificateRepository.findByCourseId(courseId);
        List<Assignment> assignments = assignmentRepository.findByCourseId(courseId);
        
        // Get submissions for all assignments in this course
        List<Submission> submissions = new java.util.ArrayList<>();
        for (Assignment assignment : assignments) {
            submissions.addAll(submissionRepository.findByAssignmentId(assignment.getAssignmentId()));
        }
        
        StringBuilder content = new StringBuilder();
        content.append("DETAILED COURSE REPORT\n");
        content.append("======================\n");
        content.append("Course ID: ").append(courseId).append("\n");
        content.append("Generated: ").append(new java.util.Date()).append("\n\n");
        
        // Enrollment Statistics
        content.append("ENROLLMENT STATISTICS\n");
        content.append("Total Enrollments: ").append(enrollments.size()).append("\n");
        long activeEnrollments = enrollments.stream().filter(e -> "ACTIVE".equals(e.getStatus())).count();
        content.append("Active Students: ").append(activeEnrollments).append("\n");
        long completedEnrollments = enrollments.stream().filter(e -> "COMPLETED".equals(e.getStatus())).count();
        content.append("Completed: ").append(completedEnrollments).append("\n");
        if (!enrollments.isEmpty()) {
            content.append("Enrollment Details:\n");
            enrollments.forEach(e -> content.append("  - Student ID: ").append(e.getStudentId())
                    .append(", Status: ").append(e.getStatus())
                    .append(", Progress: ").append(e.getProgress()).append("%\n"));
        }
        content.append("\n");
        
        // Submission Statistics
        content.append("SUBMISSION STATISTICS\n");
        content.append("Total Submissions: ").append(submissions.size()).append("\n");
        if (!submissions.isEmpty()) {
            content.append("Submission Details:\n");
            submissions.forEach(s -> content.append("  - Submission ID: ").append(s.getSubmissionId())
                    .append(", Student ID: ").append(s.getStudentId())
                    .append(", Content: ").append(s.getSubmissionContent() != null ? s.getSubmissionContent().substring(0, Math.min(50, s.getSubmissionContent().length())) + "..." : "N/A").append("\n"));
        }
        content.append("\n");
        
        // Grade Statistics
        content.append("GRADE STATISTICS\n");
        content.append("Total Grades: ").append(grades.size()).append("\n");
        double avgScore = grades.stream().mapToDouble(Grade::getMarksObtained).average().orElse(0);
        content.append("Average Score: ").append(String.format("%.2f", avgScore)).append("\n");
        long passCount = grades.stream().filter(g -> "PASS".equals(g.getGradeStatus())).count();
        long failCount = grades.stream().filter(g -> "FAIL".equals(g.getGradeStatus())).count();
        content.append("Pass Count: ").append(passCount).append("\n");
        content.append("Fail Count: ").append(failCount).append("\n\n");
        
        // Certificate Statistics
        content.append("CERTIFICATE STATISTICS\n");
        content.append("Total Certificates Generated: ").append(certificates.size()).append("\n");
        long issuedCerts = certificates.stream().filter(c -> "ISSUED".equals(c.getStatus())).count();
        long pendingCerts = certificates.stream().filter(c -> "PENDING".equals(c.getStatus())).count();
        long approvedCerts = certificates.stream().filter(c -> "APPROVED".equals(c.getApprovalStatus())).count();
        content.append("Issued Certificates: ").append(issuedCerts).append("\n");
        content.append("Pending Approval: ").append(pendingCerts).append("\n");
        content.append("Approved by Teacher: ").append(approvedCerts).append("\n");
        
        report.setReportContent(content.toString());
        report.setCourseId(courseId);
        return reportRepository.save(report);
    }

    // Generate certificate completion report
    public Report generateCertificateCompletionReport(Long adminId, Long courseId) {
        Report report = new Report("CERTIFICATE_COMPLETION", adminId);
        
        List<Certificate> certificates = certificateRepository.findByCourseId(courseId);
        
        StringBuilder content = new StringBuilder();
        content.append("CERTIFICATE COMPLETION REPORT\n");
        content.append("=============================\n");
        content.append("Course ID: ").append(courseId).append("\n");
        content.append("Report Generated: ").append(new java.util.Date()).append("\n\n");
        
        content.append("Total Certificates Generated: ").append(certificates.size()).append("\n");
        
        long issued = certificates.stream().filter(c -> "ISSUED".equals(c.getStatus())).count();
        long pending = certificates.stream().filter(c -> "PENDING".equals(c.getStatus())).count();
        long rejected = certificates.stream().filter(c -> "REJECTED".equals(c.getStatus())).count();
        
        content.append("Issued: ").append(issued).append("\n");
        content.append("Pending Approval: ").append(pending).append("\n");
        content.append("Rejected: ").append(rejected).append("\n");
        
        if (pending > 0) {
            content.append("\nPending Certificates Details:\n");
            certificates.stream()
                    .filter(c -> "PENDING".equals(c.getStatus()))
                    .forEach(c -> content.append("  - Student: ").append(c.getStudentId())
                            .append(", Certificate #: ").append(c.getCertificateNumber())
                            .append(", Score: ").append(c.getFinalScore()).append("\n"));
        }
        
        report.setReportContent(content.toString());
        report.setCourseId(courseId);
        return reportRepository.save(report);
    }

    public Report generateEnrollmentStatisticsReport(Long adminId) {
        Report report = new Report("ENROLLMENT_STATS", adminId);
        
        StringBuilder content = new StringBuilder();
        content.append("ENROLLMENT STATISTICS REPORT\n");
        content.append("============================\n");
        content.append("Total Users: ").append(userRepository.count()).append("\n");
        content.append("Report Generated By: Admin ").append(adminId).append("\n");
        content.append("Report Date: ").append(new java.util.Date()).append("\n");
        
        report.setReportContent(content.toString());
        return reportRepository.save(report);
    }

    public List<Report> getAdminReports(Long adminId) {
        return reportRepository.findByGeneratedBy(adminId);
    }

    public List<Report> getReportsByType(String reportType) {
        return reportRepository.findByReportType(reportType);
    }
}
