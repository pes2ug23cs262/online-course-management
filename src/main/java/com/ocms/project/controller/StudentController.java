package com.ocms.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ocms.project.model.Certificate;
import com.ocms.project.model.Content;
import com.ocms.project.model.CourseEntity;
import com.ocms.project.model.EnrollmentRecord;
import com.ocms.project.model.PaymentRecord;
import com.ocms.project.model.Student;
import com.ocms.project.service.CertificateService;
import com.ocms.project.service.ContentService;
import com.ocms.project.service.CourseService;
import com.ocms.project.service.DashboardService;
import com.ocms.project.service.EnrollmentService;
import com.ocms.project.service.GradingService;
import com.ocms.project.service.PaymentService;
import com.ocms.project.service.StudentService;
import com.ocms.project.service.SubmissionService;

@RestController
@RequestMapping("/api/student")
@CrossOrigin
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private GradingService gradingService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/profile/{userId}")
    public Student getStudentProfile(@PathVariable Long userId) {
        return studentService.getStudentByUserId(userId);
    }

    @GetMapping("/dashboard/{userId}")
    public java.util.Map<String, Object> getDashboard(@PathVariable Long userId) {
        return dashboardService.buildStudentDashboard(userId);
    }

    @GetMapping("/search")
    public List<CourseEntity> searchCourse(@RequestParam String keyword) {
        return courseService.listCourses().stream()
                .filter(course -> course.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || course.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    @PostMapping("/enroll")
    public String enrollCourse(@RequestParam Long studentId,
                               @RequestParam Long courseId,
                               @RequestParam(required = false) Boolean payNow,
                               @RequestParam(required = false) String paymentMethod) {
        CourseEntity course = courseService.getCourse(courseId);
        if (course == null) {
            return "Course not found.";
        }

        if (course.getPrice() > 0) {
            if (!Boolean.TRUE.equals(payNow)) {
                return "Payment is required to enroll in paid courses.";
            }
            if (paymentMethod == null || paymentMethod.isBlank()) {
                return "Payment method is required for paid courses.";
            }
            paymentService.processPayment(studentId, courseId, course.getPrice(), paymentMethod);
        }

        EnrollmentRecord enrollment = enrollmentService.enrollStudent(studentId, courseId);
        return "Enrolled student " + studentId + " into course " + courseId + ". Enrollment ID: " + enrollment.getEnrollmentId();
    }

    @PostMapping("/enrollment/{enrollmentId}/progress")
    public EnrollmentRecord updateProgress(@PathVariable Long enrollmentId,
                                           @RequestParam Double progress) {
        return enrollmentService.updateEnrollmentProgress(enrollmentId, progress);
    }

    @GetMapping("/{studentId}/content/{courseId}")
    public List<Content> accessCourseContent(@PathVariable Long studentId,
                                             @PathVariable Long courseId) {
        return contentService.getContentForCourse(courseId);
    }

    @GetMapping("/{studentId}/enrollments")
    public List<EnrollmentRecord> getEnrollments(@PathVariable Long studentId) {
        return enrollmentService.getEnrollmentsForStudent(studentId);
    }

    @GetMapping("/{studentId}/grades")
    public List<com.ocms.project.model.Grade> viewGrades(@PathVariable Long studentId) {
        return gradingService.getStudentGrades(studentId);
    }

    @GetMapping("/{studentId}/certificates")
    public List<Certificate> downloadCertificates(@PathVariable Long studentId) {
        return certificateService.getStudentCertificates(studentId);
    }

    @GetMapping("/{studentId}/payments")
    public List<PaymentRecord> getPayments(@PathVariable Long studentId) {
        return paymentService.getPaymentsForStudent(studentId);
    }

    @GetMapping("/{studentId}/gpa")
    public Double getStudentGPA(@PathVariable Long studentId) {
        return gradingService.calculateCourseGPA(studentId);
    }

    // ===================== ENHANCED CERTIFICATE MANAGEMENT ENDPOINTS =====================

    @GetMapping("/{studentId}/certificates/issued")
    public java.util.Map<String, Object> getIssuedCertificates(@PathVariable Long studentId) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        try {
            List<Certificate> certificates = certificateService.getIssuedCertificates(studentId);
            response.put("success", true);
            response.put("count", certificates.size());
            response.put("certificates", certificates);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching issued certificates: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/{studentId}/certificates/pending")
    public java.util.Map<String, Object> getPendingCertificates(@PathVariable Long studentId) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        try {
            List<Certificate> allCerts = certificateService.getStudentCertificates(studentId);
            List<Certificate> pendingCerts = allCerts.stream()
                    .filter(c -> "PENDING".equals(c.getApprovalStatus()))
                    .toList();
            response.put("success", true);
            response.put("count", pendingCerts.size());
            response.put("certificates", pendingCerts);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching pending certificates: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/certificate/{certificateId}/view")
    public java.util.Map<String, Object> viewCertificateDetails(@PathVariable Long certificateId) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        try {
            Certificate cert = certificateService.getCertificateById(certificateId);
            if (cert != null) {
                response.put("success", true);
                response.put("certificate", cert);
                response.put("certificateNumber", cert.getCertificateNumber());
                response.put("issuanceDate", cert.getIssuanceDate());
                response.put("approvalStatus", cert.getApprovalStatus());
                response.put("status", cert.getStatus());
            } else {
                response.put("success", false);
                response.put("message", "Certificate not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error viewing certificate: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/certificate/{certificateId}/download")
    public java.util.Map<String, Object> downloadCertificate(@PathVariable Long certificateId) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        try {
            Certificate cert = certificateService.getCertificateById(certificateId);
            if (cert != null && "ISSUED".equals(cert.getStatus())) {
                String certificateContent = certificateService.generateCertificateWithSignature(certificateId, "");
                String badge = certificateService.generateCertificateWithBadge(certificateId);
                response.put("success", true);
                response.put("message", "Certificate ready for download");
                response.put("certificateContent", certificateContent);
                response.put("badge", badge);
                response.put("fileName", "Certificate_" + cert.getCertificateNumber() + ".pdf");
            } else if (cert != null) {
                response.put("success", false);
                response.put("message", "Certificate is not yet issued. Status: " + cert.getStatus());
            } else {
                response.put("success", false);
                response.put("message", "Certificate not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error downloading certificate: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/{studentId}/certificates/by-course/{courseId}")
    public java.util.Map<String, Object> getCertificateForCourse(@PathVariable Long studentId,
                                                                   @PathVariable Long courseId) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        try {
            Certificate cert = certificateService.getCertificateByCourseStudent(courseId, studentId);
            if (cert != null) {
                response.put("success", true);
                response.put("certificate", cert);
                response.put("status", cert.getStatus());
                response.put("approvalStatus", cert.getApprovalStatus());
            } else {
                response.put("success", false);
                response.put("message", "No certificate found for this course");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching certificate: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/{studentId}/certificate-status")
    public java.util.Map<String, Object> getCertificateStatus(@PathVariable Long studentId) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        try {
            List<Certificate> certificates = certificateService.getStudentCertificates(studentId);
            
            long issued = certificates.stream().filter(c -> "ISSUED".equals(c.getStatus())).count();
            long pending = certificates.stream().filter(c -> "PENDING".equals(c.getStatus())).count();
            long approved = certificates.stream().filter(c -> "APPROVED".equals(c.getApprovalStatus())).count();
            long rejected = certificates.stream().filter(c -> "REJECTED".equals(c.getApprovalStatus())).count();
            
            response.put("success", true);
            response.put("totalCertificates", certificates.size());
            response.put("issued", issued);
            response.put("pending", pending);
            response.put("approved", approved);
            response.put("rejected", rejected);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching certificate status: " + e.getMessage());
        }
        return response;
    }
}
