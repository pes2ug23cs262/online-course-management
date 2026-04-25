package com.ocms.project.enrollment.model;
public class Enrollment {
    private Long studentId;
    private Long courseId;
    private String status;
    private double progress;
    private Enrollment(Builder builder) {
        this.studentId = builder.studentId;
        this.courseId = builder.courseId;
        this.status = builder.status;
        this.progress = builder.progress;
    }
    public Long getStudentId() { return studentId; }
    public Long getCourseId() { return courseId; }
    public String getStatus() { return status; }
    public double getProgress() { return progress; }
    public static class Builder {
        private Long studentId;
        private Long courseId;
        private String status;
        private double progress;
        public Builder setStudentId(Long studentId) {
            this.studentId = studentId;
            return this;
        }
        public Builder setCourseId(Long courseId) {
            this.courseId = courseId;
            return this;
        }
        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }
        public Builder setProgress(double progress) {
            this.progress = progress;
            return this;
        }
        public Enrollment build() {
            return new Enrollment(this);
        }
    }
}