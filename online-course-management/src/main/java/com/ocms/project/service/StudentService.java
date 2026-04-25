package com.ocms.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocms.project.model.Student;
import com.ocms.project.model.User;
import com.ocms.project.repository.StudentRepository;
import com.ocms.project.repository.UserRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    public Student createStudent(User user, String rollNumber, String department) {
        Student student = new Student(user, rollNumber, department);
        return studentRepository.save(student);
    }

    public Student getStudentByUserId(Long userId) {
        return studentRepository.findByUser_UserId(userId);
    }

    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId).orElse(null);
    }

    public void updateStudentGPA(Long studentId, Double gpa) {
        Student student = getStudentById(studentId);
        if (student != null) {
            student.setGpa(gpa);
            studentRepository.save(student);
        }
    }
}