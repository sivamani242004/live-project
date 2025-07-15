package com.mrtech.adminportal.service;

import com.mrtech.adminportal.entity.courses;
import com.mrtech.adminportal.repository.coursesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private coursesRepository coursesRepository;

    public List<courses> getAllCourses() {
        return coursesRepository.findAll();
    }
}
