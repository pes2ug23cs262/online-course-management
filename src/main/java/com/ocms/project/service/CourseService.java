package com.ocms.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocms.project.model.CourseEntity;
import com.ocms.project.model.CourseStatus;
import com.ocms.project.repository.CourseRepository;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public CourseEntity createCourse(Long instructorId, String title, String description, Double price) {
        CourseEntity course = new CourseEntity(title, description, price, instructorId);
        return courseRepository.save(course);
    }

    public List<CourseEntity> listCourses() {
        return courseRepository.findAll();
    }

    public CourseEntity getCourse(Long courseId) {
        return courseRepository.findById(courseId).orElse(null);
    }

    public List<CourseEntity> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }

    public List<CourseEntity> getCoursesByStatus(CourseStatus status) {
        return courseRepository.findByStatus(status);
    }

    public CourseEntity updateCourseStatus(Long courseId, CourseStatus status) {
        CourseEntity course = getCourse(courseId);
        if (course != null) {
            course.setStatus(status);
            return courseRepository.save(course);
        }
        return null;
    }

    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }
}
