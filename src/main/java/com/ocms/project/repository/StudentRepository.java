package com.ocms.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocms.project.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByUser_UserId(Long userId);
}