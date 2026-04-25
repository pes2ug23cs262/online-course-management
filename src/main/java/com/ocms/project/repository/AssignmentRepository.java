package com.ocms.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocms.project.model.Assignment;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCourseId(Long courseId);
    List<Assignment> findByTeacherId(Long teacherId);
}