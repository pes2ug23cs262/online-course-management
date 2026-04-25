package com.ocms.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ocms.project.model.Certificate;
import com.ocms.project.service.CertificateService;

@RestController
@RequestMapping("/api/certificate")
@CrossOrigin
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @PostMapping("/issue")
    public String issueCertificate(@RequestParam Long studentId,
                                  @RequestParam Long courseId,
                                  @RequestParam Double finalScore) {
        Certificate cert = certificateService.issueCertificate(studentId, courseId, finalScore);
        return "Certificate issued. ID: " + cert.getCertificateId() + ", Cert #: " + cert.getCertificateNumber();
    }

    @GetMapping("/student/{studentId}")
    public List<Certificate> getStudentCertificates(@PathVariable Long studentId) {
        return certificateService.getStudentCertificates(studentId);
    }

    @GetMapping("/generate-with-signature/{certificateId}")
    public String generateCertificateWithSignature(@PathVariable Long certificateId,
                                                  @RequestParam String signedBy) {
        return certificateService.generateCertificateWithSignature(certificateId, signedBy);
    }

    @GetMapping("/generate-with-badge/{certificateId}")
    public String generateCertificateWithBadge(@PathVariable Long certificateId) {
        return certificateService.generateCertificateWithBadge(certificateId);
    }
}