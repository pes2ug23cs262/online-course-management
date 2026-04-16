package com.ocms.project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@CrossOrigin
public class TestController {

    @GetMapping("/list")
    public List<Map<String, Object>> listTests(@RequestParam Long studentId) {
        return List.of(
                Map.of(
                        "testId", 1L,
                        "studentId", studentId,
                        "testName", "Orientation Quiz",
                        "course", "Platform Basics",
                        "status", "AVAILABLE",
                        "score", 0
                )
        );
    }

    @PostMapping("/submit")
    public String submitTest(@RequestParam Long studentId,
                             @RequestParam Long testId,
                             @RequestParam(required = false) Integer score) {
        int finalScore = score == null ? 100 : score;
        return "Test " + testId + " submitted for student " + studentId + " with score " + finalScore + ".";
    }
}
