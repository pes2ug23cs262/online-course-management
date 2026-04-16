package com.ocms.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocms.project.model.PaymentRecord;

public interface PaymentRepository extends JpaRepository<PaymentRecord, Long> {
    List<PaymentRecord> findByStudentId(Long studentId);
    List<PaymentRecord> findByCourseId(Long courseId);
}
