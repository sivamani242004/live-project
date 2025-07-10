package com.mrtech.adminportal.controller;

import com.mrtech.adminportal.entity.courses;
import com.mrtech.adminportal.repository.coursesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/courses")
public class coursesController {

    @Autowired
    private coursesRepository courseRepo;

    @PostMapping("/add")
    public courses addCourse(@RequestBody courses course) {
        return courseRepo.save(course);
    }

    @GetMapping("/all")
    public List<courses> getAllCourses() {
        return courseRepo.findAll();
    }
    
    @DeleteMapping("/delete/{id}")
    public void deleteCourse(@PathVariable("id") String id) {
        courseRepo.deleteById(id);
    }

}
