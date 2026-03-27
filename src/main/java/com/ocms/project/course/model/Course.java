package com.ocms.project.course.model;

public abstract class Course {

    protected String title;
    protected String description;

    public Course(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public abstract String getCourseType();

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
