package com.ocms.project.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ocms.project.model.CourseEntity;
import com.ocms.project.model.CourseStatus;
import com.ocms.project.service.CourseService;
import com.ocms.project.service.DashboardService;
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
}
