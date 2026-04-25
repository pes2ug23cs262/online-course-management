package com.ocms.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocms.project.model.Grade;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentId(Long studentId);
    List<Grade> findByCourseId(Long courseId);
    Grade findByStudentIdAndCourseId(Long studentId, Long courseId);
    Grade findBySubmissionId(Long submissionId);
    List<Grade> findByGradedBy(Long gradedBy);
}
