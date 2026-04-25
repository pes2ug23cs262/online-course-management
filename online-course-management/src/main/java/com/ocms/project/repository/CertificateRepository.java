package com.ocms.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocms.project.model.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByStudentId(Long studentId);
    Certificate findByCourseIdAndStudentId(Long courseId, Long studentId);
    List<Certificate> findByTeacherIdAndApprovalStatus(Long teacherId, String approvalStatus);
    List<Certificate> findByApprovalStatus(String approvalStatus);
    List<Certificate> findByStudentIdAndStatus(Long studentId, String status);
    List<Certificate> findByApprovalStatusAndStatus(String approvalStatus, String status);
    List<Certificate> findByTeacherId(Long teacherId);
    List<Certificate> findByCourseId(Long courseId);
}