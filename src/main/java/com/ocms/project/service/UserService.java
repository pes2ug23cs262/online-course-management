package com.ocms.project.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ocms.project.model.User;
import com.ocms.project.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // REGISTER
    public User register(User user) {

        // 🔹 Duplicate email check
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // 🔹 Encrypt password
        user.setPassword(encoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // LOGIN
    public User login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // 🔹 Compare encrypted password
            if (encoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }
}