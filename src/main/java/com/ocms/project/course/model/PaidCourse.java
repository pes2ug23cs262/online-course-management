package com.ocms.project.course.model;

public class PaidCourse extends Course {

    private double price;

    public PaidCourse(String title, String description, double price) {
        super(title, description);
        this.price = price;
    }

    @Override
    public String getCourseType() {
        return "PAID";
    }

    public double getPrice() {
        return price;
    }
}
