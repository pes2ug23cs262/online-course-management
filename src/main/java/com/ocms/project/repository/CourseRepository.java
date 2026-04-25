package com.ocms.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocms.project.model.CourseEntity;
import com.ocms.project.model.CourseStatus;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    List<CourseEntity> findByInstructorId(Long instructorId);
    List<CourseEntity> findByStatus(CourseStatus status);
    List<CourseEntity> findByTitle(String title);
}
