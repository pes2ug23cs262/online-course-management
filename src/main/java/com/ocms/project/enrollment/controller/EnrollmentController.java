package com.ocms.project.enrollment.controller;

import com.ocms.project.enrollment.model.Enrollment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enroll")
@CrossOrigin
public class EnrollmentController {

    @PostMapping("/create")
    public String enrollStudent(@RequestParam Long studentId,
                                @RequestParam Long courseId) {

        Enrollment enrollment = new Enrollment.Builder()
                .setStudentId(studentId)
                .setCourseId(courseId)
                .setStatus("ENROLLED")
                .setProgress(0.0)
                .build();

        return "Student " + studentId + " enrolled in course " + courseId;
    }
}