package com.ocms.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocms.project.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByReportType(String reportType);
    List<Report> findByGeneratedBy(Long adminId);
}