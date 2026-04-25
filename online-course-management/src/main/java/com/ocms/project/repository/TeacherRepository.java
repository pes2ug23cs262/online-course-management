package com.ocms.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocms.project.model.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Teacher findByUser_UserId(Long userId);
}