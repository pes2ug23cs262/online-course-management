package com.ocms.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocms.project.model.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByStudentId(Long studentId);
    Certificate findByCourseIdAndStudentId(Long courseId, Long studentId);
}