package com.ocms.project.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ocms.project.model.CourseEntity;
import com.ocms.project.model.CourseStatus;
import com.ocms.project.model.Report;
import com.ocms.project.model.Student;
import com.ocms.project.model.User;
import com.ocms.project.service.CourseService;
import com.ocms.project.service.DashboardService;
import com.ocms.project.service.ReportGenerator;
import com.ocms.project.service.StudentService;
import com.ocms.project.service.UserService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ReportGenerator reportGenerator;

    @Autowired
    private StudentService studentService;

    @GetMapping("/monitor")
    public Map<String, Object> monitorSystem() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalCourses", courseService.listCourses().size());
        summary.put("totalUsers", userService.countUsers());
        summary.put("status", "Operational");
        return summary;
    }

    @GetMapping("/dashboard/{userId}")
    public Map<String, Object> getDashboard(@PathVariable Long userId) {
        return dashboardService.buildAdminDashboard(userId);
    }

    @PostMapping("/course/{courseId}/approve")
    public String approveCourse(@PathVariable Long courseId) {
        CourseEntity course = courseService.updateCourseStatus(courseId, CourseStatus.APPROVED);
        return course == null ? "Course not found." : "Course " + course.getTitle() + " approved.";
    }

    @PostMapping("/course/{courseId}/remove")
    public String removeCourse(@PathVariable Long courseId) {
        CourseEntity course = courseService.getCourse(courseId);
        if (course == null) {
            return "Course not found.";
        }
        courseService.deleteCourse(courseId);
        return "Course " + course.getTitle() + " removed.";
    }

    // ===================== REPORT GENERATION ENDPOINTS =====================

    @PostMapping("/report/course/{courseId}")
    public Map<String, Object> generateCourseReport(@PathVariable Long courseId,
                                                      @RequestParam Long adminId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Report report = reportGenerator.generateDetailedCourseReport(adminId, courseId);
            response.put("success", true);
            response.put("message", "Course report generated successfully");
            response.put("reportId", report.getReportId());
            response.put("reportContent", report.getReportContent());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error generating course report: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/report/course-analytics/{courseId}")
    public Map<String, Object> generateCourseAnalyticsReport(@PathVariable Long courseId,
                                                              @RequestParam Long adminId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Report report = reportGenerator.generateCourseAnalyticsReport(adminId, courseId);
            response.put("success", true);
            response.put("message", "Course analytics report generated successfully");
            response.put("reportId", report.getReportId());
            response.put("reportContent", report.getReportContent());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error generating analytics report: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/report/certificate-completion/{courseId}")
    public Map<String, Object> generateCertificateReport(@PathVariable Long courseId,
                                                           @RequestParam Long adminId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Report report = reportGenerator.generateCertificateCompletionReport(adminId, courseId);
            response.put("success", true);
            response.put("message", "Certificate completion report generated successfully");
            response.put("reportId", report.getReportId());
            response.put("reportContent", report.getReportContent());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error generating certificate report: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/report/student-performance/{studentId}")
    public Map<String, Object> generateStudentPerformanceReport(@PathVariable Long studentId,
                                                                  @RequestParam Long adminId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Report report = reportGenerator.generateStudentPerformanceReport(adminId, studentId);
            response.put("success", true);
            response.put("message", "Student performance report generated successfully");
            response.put("reportId", report.getReportId());
            response.put("reportContent", report.getReportContent());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error generating student report: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/report/enrollment-stats")
    public Map<String, Object> generateEnrollmentReport(@RequestParam Long adminId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Report report = reportGenerator.generateEnrollmentStatisticsReport(adminId);
            response.put("success", true);
            response.put("message", "Enrollment statistics report generated successfully");
            response.put("reportId", report.getReportId());
            response.put("reportContent", report.getReportContent());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error generating enrollment report: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/report/generate")
    public Map<String, Object> generateReport(@RequestParam Long adminId,
                                              @RequestParam String reportType,
                                              @RequestParam(required = false) String courseTitle,
                                              @RequestParam(required = false) String studentName) {
        Map<String, Object> response = new HashMap<>();
        try {
            Report report;
            String title;
            switch (reportType) {
                case "COURSE_OVERVIEW" -> {
                    CourseEntity course = resolveCourseByTitle(courseTitle);
                    if (course == null) {
                        throw new IllegalArgumentException("Course not found for title: " + courseTitle);
                    }
                    report = reportGenerator.generateDetailedCourseReport(adminId, course.getCourseId());
                    title = "Course Overview Report";
                }
                case "STUDENT_PERFORMANCE" -> {
                    Student student = resolveStudentByName(studentName);
                    if (student == null) {
                        throw new IllegalArgumentException("Student not found for name: " + studentName);
                    }
                    report = reportGenerator.generateStudentPerformanceReport(adminId, student.getStudentId());
                    title = "Student Performance Report";
                }
                case "ENROLLMENT_SUMMARY" -> {
                    report = reportGenerator.generateEnrollmentStatisticsReport(adminId);
                    title = "Enrollment Summary Report";
                }
                default -> throw new IllegalArgumentException("Unsupported report type: " + reportType);
            }
            response.put("success", true);
            response.put("message", title + " generated successfully");
            response.put("reportId", report.getReportId());
            response.put("reportContent", report.getReportContent());
            response.put("reportType", report.getReportType());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error generating report: " + e.getMessage());
        }
        return response;
    }

    private CourseEntity resolveCourseByTitle(String courseTitle) {
        if (courseTitle == null || courseTitle.isBlank()) {
            return null;
        }
        String trimmedTitle = courseTitle.trim();
        
        // Try exact match first
        List<CourseEntity> courses = courseService.findCoursesByTitle(trimmedTitle);
        if (!courses.isEmpty()) {
            return courses.get(0);
        }
        
        // Try case-insensitive match
        List<CourseEntity> allCourses = courseService.listCourses();
        return allCourses.stream()
                .filter(c -> c.getTitle() != null && c.getTitle().equalsIgnoreCase(trimmedTitle))
                .findFirst()
                .orElse(null);
    }

    private Student resolveStudentByName(String studentName) {
        if (studentName == null || studentName.isBlank()) {
            return null;
        }
        List<User> users = userService.findUsersByName(studentName.trim());
        return users.stream()
                .map(user -> studentService.getStudentByUserId(user.getUserId()))
                .filter(student -> student != null)
                .findFirst()
                .orElse(null);
    }

    @GetMapping("/reports/{adminId}")
    public Map<String, Object> getAdminReports(@PathVariable Long adminId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Report> reports = reportGenerator.getAdminReports(adminId);
            response.put("success", true);
            response.put("totalReports", reports.size());
            response.put("reports", reports);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching reports: " + e.getMessage());
        }
        return response;
    }
}
