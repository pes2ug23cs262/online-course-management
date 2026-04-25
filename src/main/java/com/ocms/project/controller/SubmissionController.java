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

import com.ocms.project.model.Submission;
import com.ocms.project.service.SubmissionService;

@RestController
@RequestMapping("/api/submission")
@CrossOrigin
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @PostMapping("/submit")
    public String submitAssignment(@RequestParam Long assignmentId,
                                  @RequestParam Long studentId,
                                  @RequestParam String content) {
        Submission submission = submissionService.submitAssignment(assignmentId, studentId, content);
        return "Assignment submitted successfully. Submission ID: " + submission.getSubmissionId();
    }

    @PostMapping("/create")
    public String createSubmission(@RequestParam Long assignmentId,
                                   @RequestParam Long studentId,
                                   @RequestParam String content) {
        return submitAssignment(assignmentId, studentId, content);
    }

    @GetMapping("/{studentId}/submissions")
    public List<Submission> getStudentSubmissions(@PathVariable Long studentId) {
        return submissionService.getSubmissionsByStudent(studentId);
    }

    @GetMapping("/assignment/{assignmentId}")
    public List<Submission> getAssignmentSubmissions(@PathVariable Long assignmentId) {
        return submissionService.getSubmissionsForAssignment(assignmentId);
    }

    @GetMapping("/status/{submissionId}")
    public Submission getSubmissionStatus(@PathVariable Long submissionId) {
        return submissionService.getSubmissionById(submissionId);
    }
}
