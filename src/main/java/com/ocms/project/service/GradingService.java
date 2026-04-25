package com.ocms.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocms.project.model.Grade;
import com.ocms.project.model.Submission;
import com.ocms.project.repository.GradeRepository;
import com.ocms.project.repository.SubmissionRepository;

@Service
public class GradingService {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    public Grade assignGrade(Long studentId, Long courseId, Double marksObtained, Double totalMarks, Long teacherId) {
        Grade grade = gradeRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (grade == null) {
            grade = new Grade(studentId, courseId, marksObtained, totalMarks, teacherId);
        } else {
            updateGradeValues(grade, marksObtained, totalMarks, teacherId);
        }
        return gradeRepository.save(grade);
    }

    public Grade assignGradeForSubmission(Long submissionId,
                                          Long studentId,
                                          Long courseId,
                                          Double marksObtained,
                                          Double totalMarks,
                                          Long teacherId,
                                          String feedback) {
        Grade grade = gradeRepository.findBySubmissionId(submissionId);
        if (grade == null) {
            grade = new Grade(studentId, courseId, marksObtained, totalMarks, teacherId);
            grade.setSubmissionId(submissionId);
        } else {
            grade.setStudentId(studentId);
            grade.setCourseId(courseId);
            grade.setSubmissionId(submissionId);
            updateGradeValues(grade, marksObtained, totalMarks, teacherId);
        }
        grade.setFeedback(feedback);
        grade.setGradePoint(resolveGradePoint(marksObtained, totalMarks));
        Grade savedGrade = gradeRepository.save(grade);

        Submission submission = submissionRepository.findById(submissionId).orElse(null);
        if (submission != null) {
            submission.setStatus("GRADED");
            submissionRepository.save(submission);
        }
        return savedGrade;
    }

    private void updateGradeValues(Grade grade, Double marksObtained, Double totalMarks, Long teacherId) {
        grade.setMarksObtained(marksObtained);
        grade.setTotalMarks(totalMarks);
        grade.setGradedBy(teacherId);
        grade.setGradingDate(new java.util.Date());
        grade.setGradeStatus(marksObtained >= (totalMarks * 0.4) ? "PASS" : "FAIL");
        grade.setGradePoint(resolveGradePoint(marksObtained, totalMarks));
    }

    private String resolveGradePoint(Double marksObtained, Double totalMarks) {
        if (marksObtained == null || totalMarks == null || totalMarks <= 0) {
            return "";
        }
        double percentage = (marksObtained / totalMarks) * 100.0;
        if (percentage >= 90) {
            return "A+";
        }
        if (percentage >= 80) {
            return "A";
        }
        if (percentage >= 70) {
            return "B";
        }
        if (percentage >= 60) {
            return "C";
        }
        if (percentage >= 40) {
            return "D";
        }
        return "F";
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

    public Grade getGradeForSubmission(Long submissionId) {
        return gradeRepository.findBySubmissionId(submissionId);
    }

    public List<Grade> getGradesByTeacher(Long teacherId) {
        return gradeRepository.findByGradedBy(teacherId);
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
