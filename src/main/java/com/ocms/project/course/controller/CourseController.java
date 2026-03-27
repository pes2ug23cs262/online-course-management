package com.ocms.project.course.controller;

import com.ocms.project.course.factory.CourseFactory;
import com.ocms.project.course.model.Course;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course")
@CrossOrigin
public class CourseController {

    @PostMapping("/create")
    public String createCourse(@RequestParam String type,
                               @RequestParam String title,
                               @RequestParam String description,
                               @RequestParam double price) {

        Course course = CourseFactory.createCourse(type, title, description, price);

        return "Course created: " + course.getTitle() + " (" + course.getCourseType() + ")";
    }
}
