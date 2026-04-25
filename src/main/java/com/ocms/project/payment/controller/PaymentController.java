package com.ocms.project.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ocms.project.model.PaymentRecord;
import com.ocms.project.payment.PaymentGateway;
import com.ocms.project.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public String makePayment(@RequestParam(required = false) Long studentId,
                              @RequestParam(required = false) Long courseId,
                              @RequestParam String method,
                              @RequestParam double amount) {

        if (!PaymentGateway.getInstance().validatePayment(method, amount)) {
            return "Invalid payment method or amount. Supported: UPI, CreditCard, NetBanking";
        }

        if (studentId != null && courseId != null) {
            PaymentRecord payment = paymentService.processPayment(studentId, courseId, amount, method);
            return "Payment successful using " + method + " for student " + studentId + ". Payment ID: " + payment.getPaymentId();
        }

        return "Payment successful using " + method + ".";
    }

    @GetMapping("/student/{studentId}")
    public java.util.List<PaymentRecord> getPaymentsForStudent(@PathVariable Long studentId) {
        return paymentService.getPaymentsForStudent(studentId);
    }
}
