package com.ocms.project.service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocms.project.model.Admin;
import com.ocms.project.model.Assignment;
import com.ocms.project.model.Certificate;
import com.ocms.project.model.Content;
import com.ocms.project.model.CourseEntity;
import com.ocms.project.model.CourseStatus;
import com.ocms.project.model.EnrollmentRecord;
import com.ocms.project.model.EnrollmentStatus;
import com.ocms.project.model.Grade;
import com.ocms.project.model.PaymentRecord;
import com.ocms.project.model.Student;
import com.ocms.project.model.Submission;
import com.ocms.project.model.Teacher;
import com.ocms.project.model.User;
import com.ocms.project.repository.UserRepository;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private GradingService gradingService;

    @Autowired
    private ReportGenerator reportGenerator;

    public Map<String, Object> buildStudentDashboard(Long userId) {
        User user = userService.ensureRoleProfile(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found for id " + userId);
        }
        Student student = studentService.getStudentByUserId(userId);
        if (student == null) {
            throw new IllegalArgumentException("Student profile not found for user " + userId);
        }

        Long studentId = student.getStudentId();
        List<CourseEntity> allCourses = courseService.listCourses();
        List<EnrollmentRecord> enrollments = enrollmentService.getEnrollmentsForStudent(studentId);
        List<PaymentRecord> payments = paymentService.getPaymentsForStudent(studentId);
        List<Certificate> certificates = certificateService.getStudentCertificates(studentId);
        List<Grade> grades = gradingService.getStudentGrades(studentId);
        List<Submission> submissions = submissionService.getSubmissionsByStudent(studentId);

        Map<Long, CourseEntity> coursesById = allCourses.stream()
                .collect(Collectors.toMap(CourseEntity::getCourseId, course -> course));
        Map<Long, List<PaymentRecord>> paymentsByCourse = payments.stream()
                .collect(Collectors.groupingBy(PaymentRecord::getCourseId));
        Map<Long, List<Certificate>> certsByCourse = certificates.stream()
                .collect(Collectors.groupingBy(Certificate::getCourseId));
        Map<Long, List<Grade>> gradesByCourse = grades.stream()
                .collect(Collectors.groupingBy(Grade::getCourseId));
        Map<Long, List<Submission>> submissionsByAssignment = submissions.stream()
                .collect(Collectors.groupingBy(Submission::getAssignmentId));

        List<Map<String, Object>> availableCourses = allCourses.stream()
                .map(course -> toCourseMap(course))
                .toList();

        List<Map<String, Object>> enrollmentDetails = enrollments.stream()
                .map(enrollment -> {
                    CourseEntity course = coursesById.get(enrollment.getCourseId());
                    List<Assignment> assignments = assignmentService.getAssignmentsByCourse(enrollment.getCourseId());
                    List<Content> contents = contentService.getContentForCourse(enrollment.getCourseId());
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("enrollment", enrollment);
                    item.put("course", course == null ? null : toCourseMap(course));
                    item.put("contents", contents);
                    item.put("assignments", assignments.stream()
                            .map(assignment -> toAssignmentMap(
                                    assignment,
                                    submissionsByAssignment.getOrDefault(assignment.getAssignmentId(), List.of())))
                            .toList());
                    item.put("payments", paymentsByCourse.getOrDefault(enrollment.getCourseId(), List.of()));
                    item.put("certificates", certsByCourse.getOrDefault(enrollment.getCourseId(), List.of()));
                    item.put("grades", gradesByCourse.getOrDefault(enrollment.getCourseId(), List.of()));
                    return item;
                })
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("student", student);
        response.put("availableCourses", availableCourses);
        response.put("enrollments", enrollmentDetails);
        response.put("payments", payments);
        response.put("certificates", certsByCourse.values().stream()
                .flatMap(List::stream)
                .toList());
        response.put("grades", grades);
        return response;
    }

    public Map<String, Object> buildTeacherDashboard(Long userId) {
        User user = userService.ensureRoleProfile(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found for id " + userId);
        }
        Teacher teacher = teacherService.getTeacherByUserId(userId);
        if (teacher == null) {
            throw new IllegalArgumentException("Instructor profile not found for user " + userId);
        }

        Long teacherId = teacher.getTeacherId();
        List<CourseEntity> courses = courseService.getCoursesByInstructor(teacherId);
        List<Grade> teacherGrades = gradingService.getGradesByTeacher(teacherId);
        List<Certificate> teacherCertificates = certificateService.getTeacherCertificates(teacherId);
        List<Map<String, Object>> courseDetails = courses.stream()
                .sorted(Comparator.comparing(CourseEntity::getCreatedDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(course -> {
                    List<Content> contents = contentService.getContentForCourse(course.getCourseId());
                    List<Assignment> assignments = assignmentService.getAssignmentsByCourse(course.getCourseId());
                    List<EnrollmentRecord> enrollments = enrollmentService.getEnrollmentsForCourse(course.getCourseId());
                    List<Grade> courseGrades = gradingService.getCourseGrades(course.getCourseId());
                    List<Certificate> courseCertificates = teacherCertificates.stream()
                            .filter(certificate -> course.getCourseId().equals(certificate.getCourseId()))
                            .toList();
                    List<Map<String, Object>> students = enrollments.stream()
                            .map(record -> {
                                Student student = studentService.getStudentById(record.getStudentId());
                                if (student == null) {
                                    return null;
                                }
                                Map<String, Object> studentMap = new LinkedHashMap<>();
                                studentMap.put("student", student);
                                studentMap.put("user", student.getUser());
                                studentMap.put("enrollment", record);
                                studentMap.put("grades", courseGrades.stream()
                                        .filter(grade -> grade.getStudentId().equals(record.getStudentId()))
                                        .toList());
                                studentMap.put("certificate", courseCertificates.stream()
                                        .filter(certificate -> certificate.getStudentId().equals(record.getStudentId()))
                                        .findFirst()
                                        .orElse(null));
                                return studentMap;
                            })
                            .filter(item -> item != null)
                            .toList();

                    Map<String, Object> courseMap = toCourseMap(course);
                    courseMap.put("contents", contents);
                    courseMap.put("assignments", assignments.stream()
                            .map(assignment -> {
                                Map<String, Object> assignmentMap = new LinkedHashMap<>();
                                assignmentMap.put("assignment", assignment);
                                assignmentMap.put("submissions", submissionService.getSubmissionsForAssignment(assignment.getAssignmentId()).stream()
                                        .map(submission -> {
                                            Map<String, Object> submissionMap = new LinkedHashMap<>();
                                            Student student = studentService.getStudentById(submission.getStudentId());
                                            Grade grade = gradingService.getGradeForSubmission(submission.getSubmissionId());
                                            Certificate certificate = courseCertificates.stream()
                                                    .filter(item -> item.getStudentId().equals(submission.getStudentId()))
                                                    .findFirst()
                                                    .orElse(null);
                                            submissionMap.put("submission", submission);
                                            submissionMap.put("student", student);
                                            submissionMap.put("user", student != null ? student.getUser() : null);
                                            submissionMap.put("grade", grade);
                                            submissionMap.put("certificate", certificate);
                                            submissionMap.put("canAllotCertificate", grade != null && "PASS".equals(grade.getGradeStatus()));
                                            return submissionMap;
                                        })
                                        .toList());
                                return assignmentMap;
                            })
                            .toList());
                    courseMap.put("students", students);
                    courseMap.put("grades", courseGrades);
                    courseMap.put("certificates", courseCertificates);
                    courseMap.put("enrollmentCount", enrollments.size());
                    return courseMap;
                })
                .toList();

        Map<String, Object> gradeStats = new LinkedHashMap<>();
        gradeStats.put("totalGrades", teacherGrades.size());
        gradeStats.put("passCount", teacherGrades.stream().filter(grade -> "PASS".equals(grade.getGradeStatus())).count());
        gradeStats.put("failCount", teacherGrades.stream().filter(grade -> "FAIL".equals(grade.getGradeStatus())).count());
        gradeStats.put("averageMarks", teacherGrades.stream().mapToDouble(Grade::getMarksObtained).average().orElse(0.0));

        Map<String, Object> certificateStats = new LinkedHashMap<>();
        certificateStats.put("totalCertificates", teacherCertificates.size());
        certificateStats.put("pendingCertificates", teacherCertificates.stream().filter(certificate -> "PENDING".equals(certificate.getApprovalStatus())).count());
        certificateStats.put("approvedCertificates", teacherCertificates.stream().filter(certificate -> "APPROVED".equals(certificate.getApprovalStatus())).count());
        certificateStats.put("issuedCertificates", teacherCertificates.stream().filter(certificate -> "ISSUED".equals(certificate.getStatus())).count());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("teacher", teacher);
        response.put("courses", courseDetails);
        response.put("gradeStats", gradeStats);
        response.put("certificateStats", certificateStats);
        response.put("pendingCertificates", teacherCertificates.stream()
                .filter(certificate -> "PENDING".equals(certificate.getApprovalStatus()))
                .toList());
        response.put("allCourses", courseService.listCourses().stream()
                .map(this::toCourseMap)
                .toList());
        return response;
    }

    public Map<String, Object> buildAdminDashboard(Long userId) {
        User user = userService.ensureRoleProfile(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found for id " + userId);
        }
        Admin admin = adminService.getAdminByUserId(userId);
        if (admin == null) {
            throw new IllegalArgumentException("Admin profile not found for user " + userId);
        }

        List<CourseEntity> courses = courseService.listCourses();
        List<Map<String, Object>> courseSummaries = courses.stream()
                .map(course -> {
                    Map<String, Object> courseMap = toCourseMap(course);
                    courseMap.put("contents", contentService.getContentForCourse(course.getCourseId()));
                    courseMap.put("assignments", assignmentService.getAssignmentsByCourse(course.getCourseId()));
                    courseMap.put("enrollments", enrollmentService.getEnrollmentsForCourse(course.getCourseId()));
                    courseMap.put("payments", paymentService.getPaymentsForCourse(course.getCourseId()));
                    return courseMap;
                })
                .toList();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalCourses", courses.size());
        stats.put("approvedCourses", courses.stream().filter(course -> course.getStatus() == CourseStatus.APPROVED).count());
        stats.put("publishedCourses", courses.stream().filter(course -> course.getStatus() == CourseStatus.PUBLISHED).count());
        stats.put("totalEnrollments", courses.stream()
                .map(CourseEntity::getCourseId)
                .mapToLong(courseId -> enrollmentService.getEnrollmentsForCourse(courseId).size())
                .sum());
        stats.put("reportsGenerated", reportGenerator.getAdminReports(admin.getAdminId()).size());
        stats.put("status", "Operational");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("admin", admin);
        response.put("stats", stats);
        response.put("courses", courseSummaries);
        response.put("reports", reportGenerator.getAdminReports(admin.getAdminId()));
        return response;
    }

    private Map<String, Object> toCourseMap(CourseEntity course) {
        Teacher teacher = teacherService.getTeacherById(course.getInstructorId());
        User instructorUser = teacher != null ? teacher.getUser() : userRepository.findById(course.getInstructorId()).orElse(null);
        Map<String, Object> courseMap = new LinkedHashMap<>();
        courseMap.put("courseId", course.getCourseId());
        courseMap.put("title", course.getTitle());
        courseMap.put("description", course.getDescription());
        courseMap.put("price", course.getPrice());
        courseMap.put("status", course.getStatus());
        courseMap.put("type", course.getType());
        courseMap.put("createdDate", course.getCreatedDate());
        courseMap.put("instructorId", course.getInstructorId());
        courseMap.put("instructorName", instructorUser != null ? instructorUser.getName() : "Unknown");
        courseMap.put("instructorEmail", instructorUser != null ? instructorUser.getEmail() : null);
        return courseMap;
    }

    private Map<String, Object> toAssignmentMap(Assignment assignment, List<Submission> submissions) {
        Map<String, Object> assignmentMap = new LinkedHashMap<>();
        assignmentMap.put("assignment", assignment);
        assignmentMap.put("submissions", submissions);
        return assignmentMap;
    }
}
