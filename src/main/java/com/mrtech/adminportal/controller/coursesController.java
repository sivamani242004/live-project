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

    // ✅ Add new course (manual ID generation)
    @PostMapping("/add")
    public courses addCourse(@RequestBody courses course) {
        Long maxId = courseRepo.findMaxCourseId();
        if (maxId == null) {
            maxId = 0L; // ✅ Handle empty table case
        }
        course.setCourseid(maxId + 1);
        return courseRepo.save(course);
    }
    // ✅ Get all courses
    @GetMapping("/all")
    public List<courses> getAllCourses() {
        return courseRepo.findAll();
    }

    // ✅ Delete course by ID
    @DeleteMapping("/delete/{id}")
    public void deleteCourse(@PathVariable("id") Long id) {
        courseRepo.deleteById(id);
    }

    // ✅ Get only course names
    @GetMapping("/names")
    public List<String> getCourseNames() {
        return courseRepo.findAll()
                         .stream()
                         .map(courses::getCoursename)
                         .toList();
    }
}
