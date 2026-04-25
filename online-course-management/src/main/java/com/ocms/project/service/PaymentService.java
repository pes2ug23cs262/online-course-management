package com.ocms.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.ocms.project.model.PaymentRecord;
import com.ocms.project.model.PaymentStatus;
import com.ocms.project.repository.PaymentRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public PaymentRecord processPayment(Long studentId, Long courseId, Double amount, String method) {
        PaymentRecord payment = new PaymentRecord(studentId, courseId, amount, method, PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);
        payment.setStatus(PaymentStatus.SUCCESS);
        return paymentRepository.save(payment);
    }

    public List<PaymentRecord> getPaymentsForStudent(Long studentId) {
        return paymentRepository.findByStudentId(studentId);
    }

    public List<PaymentRecord> getPaymentsForCourse(Long courseId) {
        return paymentRepository.findByCourseId(courseId);
    }
}
