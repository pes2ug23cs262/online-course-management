package com.ocms.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocms.project.model.EnrollmentRecord;

public interface EnrollmentRepository extends JpaRepository<EnrollmentRecord, Long> {
    List<EnrollmentRecord> findByStudentId(Long studentId);
    List<EnrollmentRecord> findByCourseId(Long courseId);
    EnrollmentRecord findByStudentIdAndCourseId(Long studentId, Long courseId);
}
