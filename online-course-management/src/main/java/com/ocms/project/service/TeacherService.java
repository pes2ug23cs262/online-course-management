package com.ocms.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocms.project.model.Teacher;
import com.ocms.project.model.User;
import com.ocms.project.repository.TeacherRepository;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    public Teacher createTeacher(User user, String employeeId, String department) {
        Teacher teacher = new Teacher(user, employeeId, department);
        return teacherRepository.save(teacher);
    }

    public Teacher getTeacherByUserId(Long userId) {
        return teacherRepository.findByUser_UserId(userId);
    }

    public Teacher getTeacherById(Long teacherId) {
        return teacherRepository.findById(teacherId).orElse(null);
    }

    public void updateTeacherInfo(Long teacherId, String qualification, String specialization) {
        Teacher teacher = getTeacherById(teacherId);
        if (teacher != null) {
            teacher.setQualification(qualification);
            teacher.setSpecialization(specialization);
            teacherRepository.save(teacher);
        }
    }
}