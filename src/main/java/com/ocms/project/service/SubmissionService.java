package com.ocms.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocms.project.model.Submission;
import com.ocms.project.repository.SubmissionRepository;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    public Submission submitAssignment(Long assignmentId, Long studentId, String content) {
        Submission submission = new Submission(assignmentId, studentId, content);
        return submissionRepository.save(submission);
    }

    public Submission getSubmissionById(Long submissionId) {
        return submissionRepository.findById(submissionId).orElse(null);
    }

    public List<Submission> getSubmissionsForAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    public List<Submission> getSubmissionsByStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId);
    }

    public Submission getStudentSubmission(Long assignmentId, Long studentId) {
        return submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId);
    }

    public void updateSubmissionStatus(Long submissionId, String status) {
        Submission submission = getSubmissionById(submissionId);
        if (submission != null) {
            submission.setStatus(status);
            submissionRepository.save(submission);
        }
    }
}