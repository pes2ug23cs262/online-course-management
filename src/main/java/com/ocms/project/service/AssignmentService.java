package com.ocms.project.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocms.project.model.Assignment;
import com.ocms.project.repository.AssignmentRepository;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    public Assignment createAssignment(Long courseId, Long teacherId, String title, String description, Date deadline, Double totalMarks) {
        Assignment assignment = new Assignment(courseId, teacherId, title, description, deadline, totalMarks);
        return assignmentRepository.save(assignment);
    }

    public Assignment getAssignmentById(Long assignmentId) {
        return assignmentRepository.findById(assignmentId).orElse(null);
    }

    public List<Assignment> getAssignmentsByCourse(Long courseId) {
        return assignmentRepository.findByCourseId(courseId);
    }

    public List<Assignment> getAssignmentsByTeacher(Long teacherId) {
        return assignmentRepository.findByTeacherId(teacherId);
    }

    public void updateAssignmentStatus(Long assignmentId, String status) {
        Assignment assignment = getAssignmentById(assignmentId);
        if (assignment != null) {
            assignment.setStatus(status);
            assignmentRepository.save(assignment);
        }
    }
}