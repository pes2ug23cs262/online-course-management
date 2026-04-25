package com.ocms.project.course.model;

public class FreeCourse extends Course {

    public FreeCourse(String title, String description) {
        super(title, description);
    }

    @Override
    public String getCourseType() {
        return "FREE";
    }
}
