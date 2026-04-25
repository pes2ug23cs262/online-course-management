package com.ocms.project.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ocms.project.model.Assignment;
import com.ocms.project.model.Certificate;
import com.ocms.project.model.CourseEntity;
import com.ocms.project.model.EnrollmentRecord;
import com.ocms.project.model.Grade;
import com.ocms.project.model.Submission;
import com.ocms.project.model.Teacher;
import com.ocms.project.model.User;
import com.ocms.project.repository.UserRepository;
import com.ocms.project.service.AssignmentService;
import com.ocms.project.service.CertificateService;
import com.ocms.project.service.ContentService;
import com.ocms.project.service.CourseService;
import com.ocms.project.service.DashboardService;
import com.ocms.project.service.EnrollmentService;
import com.ocms.project.service.GradingService;
import com.ocms.project.service.SubmissionService;
import com.ocms.project.service.TeacherService;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private GradingService gradingService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/profile/{userId}")
    public Teacher getTeacherProfile(@PathVariable Long userId) {
        return teacherService.getTeacherByUserId(userId);
    }

    @GetMapping("/dashboard/{userId}")
    public Map<String, Object> getDashboard(@PathVariable Long userId) {
        return dashboardService.buildTeacherDashboard(userId);
    }

    @PostMapping("/assignment/create")
    public String createAssignment(@RequestParam Long courseId,
                                  @RequestParam Long teacherId,
                                  @RequestParam String title,
                                  @RequestParam String description,
                                  @RequestParam Long deadline,
                                  @RequestParam Double totalMarks) {
        Assignment assignment = assignmentService.createAssignment(courseId, teacherId, title, description, new Date(deadline), totalMarks);
        return "Assignment created with ID: " + assignment.getAssignmentId();
    }

    @GetMapping("/assignment/{assignmentId}/submissions")
    public List<Submission> getSubmissionsForAssignment(@PathVariable Long assignmentId) {
        return submissionService.getSubmissionsForAssignment(assignmentId);
    }

    @PostMapping("/grade")
    public String assignGrade(@RequestParam Long studentId,
                             @RequestParam Long courseId,
                             @RequestParam Double marksObtained,
                             @RequestParam Double totalMarks,
                             @RequestParam Long teacherId,
                             @RequestParam(required = false) String feedback) {
        Grade grade = gradingService.assignGrade(studentId, courseId, marksObtained, totalMarks, teacherId);
        if (feedback != null) {
            gradingService.updateGradeFeedback(grade.getGradeId(), feedback, "");
        }
        return "Grade assigned successfully. Grade ID: " + grade.getGradeId();
    }

    @PostMapping("/course/create")
    public String createCourse(@RequestParam Long instructorId,
                               @RequestParam String title,
                               @RequestParam String description,
                               @RequestParam Double price) {
        CourseEntity course = courseService.createCourse(instructorId, title, description, price);
        return "Created course: " + course.getTitle() + " (" + course.getType() + ")";
    }

    @PostMapping("/course/{courseId}/content")
    public String uploadCourseContent(@PathVariable Long courseId,
                                      @RequestParam String title,
                                      @RequestParam String type,
                                      @RequestParam String url) {
        CourseEntity course = courseService.getCourse(courseId);
        if (course == null) {
            return "Course not found.";
        }
        contentService.addCourseContent(courseId, title, type, url);
        return "Content uploaded to course " + course.getTitle() + ".";
    }

    @PostMapping("/assignment/{assignmentId}/grade")
    public Map<String, Object> evaluateAssignment(@PathVariable Long assignmentId,
                                                  @RequestParam Long submissionId,
                                                  @RequestParam Double marksObtained,
                                                  @RequestParam(required = false) Double totalMarks,
                                                  @RequestParam Long teacherId,
                                                  @RequestParam(required = false) String feedback) {
        Map<String, Object> response = new HashMap<>();
        try {
            Assignment assignment = assignmentService.getAssignmentById(assignmentId);
            Submission submission = submissionService.getSubmissionById(submissionId);
            if (assignment == null || submission == null || !assignmentId.equals(submission.getAssignmentId())) {
                response.put("success", false);
                response.put("message", "Assignment or submission not found.");
                return response;
            }

            Double maxMarks = totalMarks != null ? totalMarks : assignment.getTotalMarks();
            Grade grade = gradingService.assignGradeForSubmission(
                    submissionId,
                    submission.getStudentId(),
                    assignment.getCourseId(),
                    marksObtained,
                    maxMarks,
                    teacherId,
                    feedback);

            response.put("success", true);
            response.put("message", "Student graded successfully.");
            response.put("grade", grade);
            response.put("submissionId", submissionId);
            response.put("studentId", submission.getStudentId());
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error grading submission: " + e.getMessage());
            return response;
        }
    }

    @PostMapping("/assignment/{assignmentId}/certificate/allot")
    public Map<String, Object> allotCertificate(@PathVariable Long assignmentId,
                                                @RequestParam Long submissionId,
                                                @RequestParam Long teacherId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Assignment assignment = assignmentService.getAssignmentById(assignmentId);
            Submission submission = submissionService.getSubmissionById(submissionId);
            if (assignment == null || submission == null || !assignmentId.equals(submission.getAssignmentId())) {
                response.put("success", false);
                response.put("message", "Assignment or submission not found.");
                return response;
            }

            Grade grade = gradingService.getGradeForSubmission(submissionId);
            if (grade == null) {
                response.put("success", false);
                response.put("message", "Grade the student before allotting a certificate.");
                return response;
            }
            if (!"PASS".equals(grade.getGradeStatus())) {
                response.put("success", false);
                response.put("message", "Certificate can only be allotted for passing grades.");
                return response;
            }

            Certificate certificate = certificateService.generateCertificateForCompletion(
                    submission.getStudentId(),
                    assignment.getCourseId(),
                    teacherId,
                    grade.getMarksObtained());
            response.put("success", true);
            response.put("message", "Certificate allotted and moved to teacher approval.");
            response.put("certificate", certificate);
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error allotting certificate: " + e.getMessage());
            return response;
        }
    }

    @PostMapping("/course/{courseId}/publish")
    public String publishCourse(@PathVariable Long courseId) {
        CourseEntity course = courseService.updateCourseStatus(courseId, com.ocms.project.model.CourseStatus.PUBLISHED);
        return course == null ? "Course not found." : "Course " + course.getTitle() + " published.";
    }

    @GetMapping("/course/{courseId}/students")
    public List<Map<String, Object>> getCourseStudents(@PathVariable Long courseId) {
        List<EnrollmentRecord> enrollments = enrollmentService.getEnrollmentsForCourse(courseId);
        return enrollments.stream()
                .map(record -> {
                    User user = userRepository.findById(record.getStudentId()).orElse(null);
                    if (user == null) {
                        return null;
                    }
                    Map<String, Object> studentInfo = new HashMap<>();
                    studentInfo.put("userId", user.getUserId());
                    studentInfo.put("name", user.getName());
                    studentInfo.put("email", user.getEmail());
                    studentInfo.put("status", record.getStatus());
                    studentInfo.put("enrollmentDate", record.getEnrollmentDate());
                    studentInfo.put("courseId", record.getCourseId());
                    return studentInfo;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @GetMapping("/course/{courseId}/assignments")
    public List<Assignment> getAssignmentsForCourse(@PathVariable Long courseId) {
        return assignmentService.getAssignmentsByCourse(courseId);
    }

    @PostMapping("/course/{courseId}/assignment/create")
    public String createCourseAssignment(@PathVariable Long courseId,
                                         @RequestParam Long teacherId,
                                         @RequestParam String title,
                                         @RequestParam String description,
                                         @RequestParam Long deadline,
                                         @RequestParam Double totalMarks) {
        Assignment assignment = assignmentService.createAssignment(courseId, teacherId, title, description, new Date(deadline), totalMarks);
        return "Assignment created with ID: " + assignment.getAssignmentId();
    }

    @GetMapping("/course/{courseId}/submissions")
    public List<Submission> getCourseSubmissions(@PathVariable Long courseId) {
        List<Assignment> assignments = assignmentService.getAssignmentsByCourse(courseId);
        return assignments.stream()
                .flatMap(assignment -> submissionService.getSubmissionsForAssignment(assignment.getAssignmentId()).stream())
                .collect(Collectors.toList());
    }

    @GetMapping("/courses/{teacherId}")
    public List<CourseEntity> getTeacherCourses(@PathVariable Long teacherId) {
        return courseService.getCoursesByInstructor(teacherId);
    }

    // ===================== CERTIFICATE MANAGEMENT ENDPOINTS =====================

    @GetMapping("/certificates/pending/{teacherId}")
    public Map<String, Object> getPendingCertificatesForApproval(@PathVariable Long teacherId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<com.ocms.project.model.Certificate> pendingCerts = certificateService.getPendingCertificatesForTeacher(teacherId);
            response.put("success", true);
            response.put("count", pendingCerts.size());
            response.put("certificates", pendingCerts);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching pending certificates: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/certificate/{certificateId}/approve")
    public Map<String, Object> approveCertificate(@PathVariable Long certificateId,
                                                    @RequestParam Long teacherId,
                                                    @RequestParam(required = false) String comments) {
        Map<String, Object> response = new HashMap<>();
        try {
            com.ocms.project.model.Certificate cert = certificateService.approveCertificate(certificateId, teacherId, comments);
            if (cert != null) {
                response.put("success", true);
                response.put("message", "Certificate approved successfully");
                response.put("certificate", cert);
            } else {
                response.put("success", false);
                response.put("message", "Certificate not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error approving certificate: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/certificate/{certificateId}/reject")
    public Map<String, Object> rejectCertificate(@PathVariable Long certificateId,
                                                   @RequestParam Long teacherId,
                                                   @RequestParam String reason) {
        Map<String, Object> response = new HashMap<>();
        try {
            com.ocms.project.model.Certificate cert = certificateService.rejectCertificate(certificateId, teacherId, reason);
            if (cert != null) {
                response.put("success", true);
                response.put("message", "Certificate rejected successfully");
                response.put("certificate", cert);
            } else {
                response.put("success", false);
                response.put("message", "Certificate not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error rejecting certificate: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/certificate/{certificateId}/send")
    public Map<String, Object> sendCertificateToStudent(@PathVariable Long certificateId) {
        Map<String, Object> response = new HashMap<>();
        try {
            com.ocms.project.model.Certificate cert = certificateService.sendCertificateToStudent(certificateId);
            if (cert != null) {
                response.put("success", true);
                response.put("message", "Certificate sent to student successfully");
                response.put("sentDate", cert.getSentDate());
                response.put("studentId", cert.getStudentId());
                response.put("certificate", cert);
            } else {
                response.put("success", false);
                response.put("message", "Certificate not found or not approved");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error sending certificate: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/certificate/generate/{studentId}/{courseId}")
    public Map<String, Object> generateCertificateForCompletion(@PathVariable Long studentId,
                                                                  @PathVariable Long courseId,
                                                                  @RequestParam Long teacherId,
                                                                  @RequestParam Double finalScore) {
        Map<String, Object> response = new HashMap<>();
        try {
            com.ocms.project.model.Certificate cert = certificateService.generateCertificateForCompletion(studentId, courseId, teacherId, finalScore);
            response.put("success", true);
            response.put("message", "Certificate generated for course completion");
            response.put("certificateId", cert.getCertificateId());
            response.put("certificate", cert);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error generating certificate: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/certificate/{certificateId}")
    public Map<String, Object> viewCertificateDetails(@PathVariable Long certificateId) {
        Map<String, Object> response = new HashMap<>();
        try {
            com.ocms.project.model.Certificate cert = certificateService.getCertificateById(certificateId);
            if (cert != null) {
                response.put("success", true);
                response.put("certificate", cert);
            } else {
                response.put("success", false);
                response.put("message", "Certificate not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching certificate: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/certificate/{certificateId}/formatted")
    public Map<String, Object> getCertificateFormatted(@PathVariable Long certificateId,
                                                        @RequestParam(required = false) String withSignature) {
        Map<String, Object> response = new HashMap<>();
        try {
            com.ocms.project.model.Certificate cert = certificateService.getCertificateById(certificateId);
            if (cert != null) {
                String formattedContent;
                if ("true".equals(withSignature)) {
                    String teacherName = "Teacher"; // You can fetch from userRepository if needed
                    formattedContent = certificateService.generateCertificateWithSignature(certificateId, teacherName);
                } else {
                    formattedContent = certificateService.generateCertificateWithSignature(certificateId, "");
                }
                response.put("success", true);
                response.put("content", formattedContent);
                response.put("badge", certificateService.generateCertificateWithBadge(certificateId));
            } else {
                response.put("success", false);
                response.put("message", "Certificate not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error formatting certificate: " + e.getMessage());
        }
        return response;
    }
}
