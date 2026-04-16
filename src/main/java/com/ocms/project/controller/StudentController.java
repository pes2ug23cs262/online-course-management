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
}
