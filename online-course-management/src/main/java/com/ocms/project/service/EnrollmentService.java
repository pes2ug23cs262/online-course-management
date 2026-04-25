package com.ocms.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.ocms.project.model.EnrollmentRecord;
import com.ocms.project.model.EnrollmentStatus;
import com.ocms.project.repository.EnrollmentRepository;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    public EnrollmentRecord enrollStudent(Long studentId, Long courseId) {
        EnrollmentRecord existing = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (existing != null) {
            return existing;
        }
        EnrollmentRecord record = new EnrollmentRecord(studentId, courseId);
        return enrollmentRepository.save(record);
    }

    public List<EnrollmentRecord> getEnrollmentsForStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<EnrollmentRecord> getEnrollmentsForCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    public EnrollmentRecord updateEnrollmentProgress(Long enrollmentId, Double progress) {
        EnrollmentRecord enrollment = enrollmentRepository.findById(enrollmentId).orElse(null);
        if (enrollment != null) {
            enrollment.setProgress(progress);
            if (progress >= 100.0) {
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
            }
            return enrollmentRepository.save(enrollment);
        }
        return null;
    }

    public EnrollmentRecord updateEnrollmentStatus(Long enrollmentId, EnrollmentStatus status) {
        EnrollmentRecord enrollment = enrollmentRepository.findById(enrollmentId).orElse(null);
        if (enrollment != null) {
            enrollment.setStatus(status);
            return enrollmentRepository.save(enrollment);
        }
        return null;
    }
}
