package com.ocms.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ocms.project.dto.LoginRequest;
import com.ocms.project.dto.RegisterRequest;
import com.ocms.project.model.User;
import com.ocms.project.model.UserStatus;
import com.ocms.project.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @GetMapping("/test")
    public String test() {
        return "Backend is working!";
    }

    @Autowired
    private UserService userService;

    // REGISTER
@PostMapping("/register")
public Object register(@RequestBody RegisterRequest request) {

        // 🔹 Validation
        if (request.name == null || request.name.isEmpty()) {
            return "Name is required";
        }

        if (request.email == null || request.email.isEmpty()) {
            return "Email is required";
        }

        if (request.password == null || request.password.length() < 4) {
            return "Password must be at least 4 characters";
        }

        if (request.role == null) {
            return "Role is required";
        }

        try {
            User user = new User(
                    request.name,
                    request.email,
                    request.password,
                    request.role,
                    UserStatus.ACTIVE
            );

            User savedUser = userService.register(user);

            return userService.buildSession(savedUser);

        } catch (RuntimeException e) {
            return e.getMessage(); // duplicate email message
        }
    }

    // LOGIN
    @PostMapping("/login")
    public Object login(@RequestBody LoginRequest request) {

        if (request.email == null || request.password == null) {
            return "Email and password are required";
        }

        User user = userService.login(request.email, request.password);

        if (user != null) {
            return userService.buildSession(user);
        } else {
            return "Invalid credentials";
        }
    }
}
