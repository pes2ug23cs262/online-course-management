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
    public String evaluateAssignment(@RequestParam Long studentId,
                                     @RequestParam Long courseId,
                                     @RequestParam Double marksObtained,
                                     @RequestParam Double totalMarks,
                                     @RequestParam Long teacherId,
                                     @RequestParam(required = false) String feedback) {
        Grade grade = gradingService.assignGrade(studentId, courseId, marksObtained, totalMarks, teacherId);
        if (feedback != null) {
            gradingService.updateGradeFeedback(grade.getGradeId(), feedback, "");
        }
        if ("PASS".equals(grade.getGradeStatus())) {
            certificateService.issueCertificate(studentId, courseId, marksObtained);
        }
        return "Evaluation completed. Grade ID: " + grade.getGradeId();
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
}
