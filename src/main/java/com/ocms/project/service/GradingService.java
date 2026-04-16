package com.ocms.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocms.project.model.Grade;
import com.ocms.project.repository.GradeRepository;

@Service
public class GradingService {

    @Autowired
    private GradeRepository gradeRepository;

    public Grade assignGrade(Long studentId, Long courseId, Double marksObtained, Double totalMarks, Long teacherId) {
        Grade grade = new Grade(studentId, courseId, marksObtained, totalMarks, teacherId);
        return gradeRepository.save(grade);
    }

    public Grade getGradeById(Long gradeId) {
        return gradeRepository.findById(gradeId).orElse(null);
    }

    public List<Grade> getStudentGrades(Long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    public List<Grade> getCourseGrades(Long courseId) {
        return gradeRepository.findByCourseId(courseId);
    }

    public Grade getGradeForStudentCourse(Long studentId, Long courseId) {
        return gradeRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    public void updateGradeFeedback(Long gradeId, String feedback, String gradePoint) {
        Grade grade = getGradeById(gradeId);
        if (grade != null) {
            grade.setFeedback(feedback);
            grade.setGradePoint(gradePoint);
            gradeRepository.save(grade);
        }
    }

    public Double calculateCourseGPA(Long studentId) {
        List<Grade> grades = getStudentGrades(studentId);
        if (grades.isEmpty()) return 0.0;
        
        double totalGPA = 0;
        for (Grade g : grades) {
            double percentage = (g.getMarksObtained() / g.getTotalMarks()) * 100;
            totalGPA += percentage;
        }
        return totalGPA / grades.size();
    }
}