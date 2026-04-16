package com.ocms.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocms.project.model.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByCourseId(Long courseId);
}
