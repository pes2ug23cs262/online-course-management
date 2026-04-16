package com.ocms.project.enrollment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ocms.project.model.EnrollmentRecord;
import com.ocms.project.service.EnrollmentService;

@RestController
@RequestMapping("/api/enroll")
@CrossOrigin
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/create")
    public String enrollStudent(@RequestParam Long studentId,
                                @RequestParam Long courseId) {
        EnrollmentRecord record = enrollmentService.enrollStudent(studentId, courseId);
        return "Student " + studentId + " enrolled in course " + courseId + ". Enrollment ID: " + record.getEnrollmentId();
    }

    @GetMapping("/list")
    public List<EnrollmentRecord> listEnrollments(@RequestParam Long studentId) {
        return enrollmentService.getEnrollmentsForStudent(studentId);
    }
}
