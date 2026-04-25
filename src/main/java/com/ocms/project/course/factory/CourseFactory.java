package com.ocms.project.course.factory;

import com.ocms.project.course.model.*;

public class CourseFactory {

    public static Course createCourse(String type, String title, String desc, double price) {

        if (type.equalsIgnoreCase("FREE")) {
            return new FreeCourse(title, desc);
        } 
        else if (type.equalsIgnoreCase("PAID")) {
            return new PaidCourse(title, desc, price);
        }

        throw new IllegalArgumentException("Invalid course type");
    }
}
