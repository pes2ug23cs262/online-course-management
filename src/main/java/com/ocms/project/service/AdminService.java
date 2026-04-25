package com.ocms.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocms.project.model.Admin;
import com.ocms.project.model.User;
import com.ocms.project.repository.AdminRepository;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public Admin createAdmin(User user, String employeeId, String department) {
        Admin admin = new Admin(user, employeeId, department);
        return adminRepository.save(admin);
    }

    public Admin getAdminByUserId(Long userId) {
        return adminRepository.findByUser_UserId(userId);
    }

    public Admin getAdminById(Long adminId) {
        return adminRepository.findById(adminId).orElse(null);
    }

    public void manageUser(Long userId, String action) {
        // Admin can suspend/activate users
        System.out.println("Admin action: " + action + " on user: " + userId);
    }
}