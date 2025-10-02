package com.mrtech.adminportal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
public class courses {

    @Id
    private Long courseid;  // ✅ manually set

    private String coursename;
    private int duration;
    private int coursefee;

    // Getters & Setters
    public Long getCourseid() {
        return courseid;
    }

    public void setCourseid(Long courseid) {
        this.courseid = courseid;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCoursefee() {
        return coursefee;
    }

    public void setCoursefee(int coursefee) {
        this.coursefee = coursefee;
    }
}
