package com.ocms.project.service;

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

    public Certificate issueCertificate(Long studentId, Long courseId, Double finalScore) {
        Certificate cert = new Certificate(studentId, courseId, finalScore);
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

    // Decorator: Add signature to certificate
    public String generateCertificateWithSignature(Long certificateId, String signedBy) {
        Certificate cert = getCertificateById(certificateId);
        if (cert != null) {
            String certificateContent = "CERTIFICATE OF COMPLETION\n" +
                    "Student ID: " + cert.getStudentId() + "\n" +
                    "Course ID: " + cert.getCourseId() + "\n" +
                    "Score: " + cert.getFinalScore() + "\n" +
                    "Cert #: " + cert.getCertificateNumber() + "\n" +
                    "Signed By: " + signedBy + "\n" +
                    "Date: " + cert.getIssuanceDate();
            return certificateContent;
        }
        return "";
    }

    // Decorator: Add digital badge
    public String generateCertificateWithBadge(Long certificateId) {
        Certificate cert = getCertificateById(certificateId);
        if (cert != null && cert.getFinalScore() >= 80) {
            return "🏆 ACHIEVEMENT UNLOCKED - Badge: Excellence in " + cert.getCourseId();
        }
        return "";
    }
}