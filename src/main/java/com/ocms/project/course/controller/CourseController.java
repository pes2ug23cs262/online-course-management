package com.ocms.project.course.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ocms.project.model.Content;
import com.ocms.project.model.CourseEntity;
import com.ocms.project.service.ContentService;
import com.ocms.project.service.CourseService;

@RestController
@RequestMapping("/api/course")
@CrossOrigin
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private ContentService contentService;

    @PostMapping("/create")
    public String createCourse(@RequestParam Long instructorId,
                               @RequestParam String title,
                               @RequestParam String description,
                               @RequestParam double price) {
        CourseEntity course = courseService.createCourse(instructorId, title, description, price);
        return "Course created: " + course.getTitle() + " (" + course.getType() + ")";
    }

    @GetMapping("/list")
    public List<CourseEntity> listCourses() {
        return courseService.listCourses();
    }

    @GetMapping("/{courseId}")
    public CourseEntity getCourse(@PathVariable Long courseId) {
        return courseService.getCourse(courseId);
    }

    @GetMapping("/search")
    public List<CourseEntity> searchCourses(@RequestParam String keyword) {
        return courseService.listCourses().stream()
                .filter(course -> course.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || course.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    @PostMapping("/{courseId}/content")
    public String addCourseContent(@PathVariable Long courseId,
                                   @RequestParam String title,
                                   @RequestParam String type,
                                   @RequestParam String url) {
        Content content = contentService.addCourseContent(courseId, title, type, url);
        return "Content added to course " + courseId + " with content ID " + content.getContentId();
    }

    @GetMapping("/{courseId}/content")
    public List<Content> getCourseContent(@PathVariable Long courseId) {
        return contentService.getContentForCourse(courseId);
    }
}
