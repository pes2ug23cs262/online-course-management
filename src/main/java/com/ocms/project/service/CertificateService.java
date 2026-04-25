package com.ocms.project.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocms.project.model.Certificate;
import com.ocms.project.repository.CertificateRepository;

/**
 * CertificateService - Uses Decorator Pattern
 * Decorates certificate with additional metadata
 */
@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    // Generate certificate when assignment is completed
    public Certificate generateCertificateForCompletion(Long studentId, Long courseId, Long teacherId, Double finalScore) {
        Certificate cert = certificateRepository.findByCourseIdAndStudentId(courseId, studentId);
        if (cert == null) {
            cert = new Certificate(studentId, courseId, teacherId, finalScore);
        } else {
            cert.setTeacherId(teacherId);
            cert.setFinalScore(finalScore);
            cert.setIssuanceDate(new Date());
            cert.setStatus("PENDING");
            cert.setApprovalStatus("PENDING");
            cert.setApprovedBy(null);
            cert.setApprovalDate(null);
            cert.setApprovalComments(null);
            cert.setSentDate(null);
        }
        return certificateRepository.save(cert);
    }

    public Certificate issueCertificate(Long studentId, Long courseId, Double finalScore) {
        Certificate cert = new Certificate(studentId, courseId, null, finalScore);
        return certificateRepository.save(cert);
    }

    public Certificate getCertificateById(Long certificateId) {
        return certificateRepository.findById(certificateId).orElse(null);
    }

    public List<Certificate> getStudentCertificates(Long studentId) {
        return certificateRepository.findByStudentId(studentId);
    }

    public Certificate getCertificateByCourseStudent(Long courseId, Long studentId) {
        return certificateRepository.findByCourseIdAndStudentId(courseId, studentId);
    }

    // Get pending certificates for teacher approval
    public List<Certificate> getPendingCertificatesForTeacher(Long teacherId) {
        return certificateRepository.findByTeacherIdAndApprovalStatus(teacherId, "PENDING");
    }

    // Get all pending certificates
    public List<Certificate> getAllPendingCertificates() {
        return certificateRepository.findByApprovalStatus("PENDING");
    }

    // Teacher approves certificate
    public Certificate approveCertificate(Long certificateId, Long teacherId, String comments) {
        Certificate cert = getCertificateById(certificateId);
        if (cert != null && (cert.getTeacherId() == null || cert.getTeacherId().equals(teacherId))) {
            cert.setTeacherId(teacherId);
            cert.setApprovalStatus("APPROVED");
            cert.setApprovedBy(teacherId);
            cert.setApprovalDate(new Date());
            cert.setApprovalComments(comments);
            cert.setStatus("APPROVED");
            return certificateRepository.save(cert);
        }
        return null;
    }

    // Teacher rejects certificate
    public Certificate rejectCertificate(Long certificateId, Long teacherId, String reason) {
        Certificate cert = getCertificateById(certificateId);
        if (cert != null && (cert.getTeacherId() == null || cert.getTeacherId().equals(teacherId))) {
            cert.setTeacherId(teacherId);
            cert.setApprovalStatus("REJECTED");
            cert.setApprovedBy(teacherId);
            cert.setApprovalDate(new Date());
            cert.setApprovalComments("REJECTED - " + reason);
            cert.setStatus("REJECTED");
            return certificateRepository.save(cert);
        }
        return null;
    }

    // Send approved certificate to student
    public Certificate sendCertificateToStudent(Long certificateId) {
        Certificate cert = getCertificateById(certificateId);
        if (cert != null && "APPROVED".equals(cert.getApprovalStatus())) {
            cert.setStatus("ISSUED");
            cert.setSentDate(new Date());
            return certificateRepository.save(cert);
        }
        return null;
    }

    public List<Certificate> getTeacherCertificates(Long teacherId) {
        return certificateRepository.findByTeacherId(teacherId);
    }

    // Get issued certificates by student
    public List<Certificate> getIssuedCertificates(Long studentId) {
        return certificateRepository.findByStudentIdAndStatus(studentId, "ISSUED");
    }

    // Get approved certificates for sending
    public List<Certificate> getApprovedCertificatesForSending() {
        return certificateRepository.findByApprovalStatusAndStatus("APPROVED", "APPROVED");
    }

    // Decorator: Add signature to certificate
    public String generateCertificateWithSignature(Long certificateId, String signedBy) {
        Certificate cert = getCertificateById(certificateId);
        if (cert != null) {
            String certificateContent = "========================================\n" +
                    "CERTIFICATE OF COMPLETION\n" +
                    "========================================\n" +
                    "Student ID: " + cert.getStudentId() + "\n" +
                    "Course ID: " + cert.getCourseId() + "\n" +
                    "Score: " + cert.getFinalScore() + "\n" +
                    "Certificate #: " + cert.getCertificateNumber() + "\n" +
                    "Issued Date: " + cert.getIssuanceDate() + "\n" +
                    "Approved By: " + signedBy + "\n" +
                    "Approval Date: " + cert.getApprovalDate() + "\n" +
                    "Status: " + cert.getStatus() + "\n" +
                    "========================================";
            return certificateContent;
        }
        return "";
    }

    // Decorator: Add digital badge
    public String generateCertificateWithBadge(Long certificateId) {
        Certificate cert = getCertificateById(certificateId);
        if (cert != null && cert.getFinalScore() >= 80) {
            return "🏆 ACHIEVEMENT UNLOCKED - Badge: Excellence in Course " + cert.getCourseId();
        }
        return "";
    }
}
