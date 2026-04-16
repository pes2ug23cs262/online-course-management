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

import com.ocms.project.model.Assignment;
import com.ocms.project.service.AssignmentService;

@RestController
@RequestMapping("/api/assignment")
@CrossOrigin
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @GetMapping("/list")
    public List<Assignment> listAssignments(@RequestParam Long courseId) {
        return assignmentService.getAssignmentsByCourse(courseId);
    }

    @GetMapping("/{assignmentId}")
    public Assignment getAssignment(@PathVariable Long assignmentId) {
        return assignmentService.getAssignmentById(assignmentId);
    }

    @PostMapping("/create")
    public String createAssignment(@RequestParam Long courseId,
                                   @RequestParam Long teacherId,
                                   @RequestParam String title,
                                   @RequestParam String description,
                                   @RequestParam Long deadline,
                                   @RequestParam Double totalMarks) {
        Assignment assignment = assignmentService.createAssignment(
                courseId,
                teacherId,
                title,
                description,
                new java.util.Date(deadline),
                totalMarks
        );
        return "Assignment created with ID: " + assignment.getAssignmentId();
    }
}
