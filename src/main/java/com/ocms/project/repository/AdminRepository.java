package com.ocms.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocms.project.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByUser_UserId(Long userId);
}