package com.ocms.project.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ocms.project.dto.UserSessionResponse;
import com.ocms.project.model.Admin;
import com.ocms.project.model.Role;
import com.ocms.project.model.Student;
import com.ocms.project.model.Teacher;
import com.ocms.project.model.User;
import com.ocms.project.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // ✅ injected, not new

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private AdminService adminService;

    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        createRoleProfile(savedUser);
        return savedUser;
    }

    public User login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                ensureRoleProfile(user);
                return user;
            }
        }
        return null;
    }

    public long countUsers() {
        return userRepository.count();
    }

    public UserSessionResponse buildSession(User user) {
        ensureRoleProfile(user);
        UserSessionResponse response = new UserSessionResponse();
        response.setUserId(user.getUserId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());

        Student student = studentService.getStudentByUserId(user.getUserId());
        Teacher teacher = teacherService.getTeacherByUserId(user.getUserId());
        Admin admin = adminService.getAdminByUserId(user.getUserId());
        response.setStudentId(student != null ? student.getStudentId() : null);
        response.setTeacherId(teacher != null ? teacher.getTeacherId() : null);
        response.setAdminId(admin != null ? admin.getAdminId() : null);
        return response;
    }

    public void ensureRoleProfile(User user) {
        if (user == null) {
            return;
        }
        createRoleProfile(user);
    }

    public User ensureRoleProfile(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = userRepository.findById(userId).orElse(null);
        ensureRoleProfile(user);
        return user;
    }

    private void createRoleProfile(User user) {
        if (user.getRole() == Role.STUDENT && studentService.getStudentByUserId(user.getUserId()) == null) {
            studentService.createStudent(user, "ROLL-" + user.getUserId(), "General");
        } else if (user.getRole() == Role.INSTRUCTOR && teacherService.getTeacherByUserId(user.getUserId()) == null) {
            teacherService.createTeacher(user, "EMP-" + user.getUserId(), "Academic");
        } else if (user.getRole() == Role.ADMIN && adminService.getAdminByUserId(user.getUserId()) == null) {
            adminService.createAdmin(user, "ADM-" + user.getUserId(), "Administration");
        }
    }
}
