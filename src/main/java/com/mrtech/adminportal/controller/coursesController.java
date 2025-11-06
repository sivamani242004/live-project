package com.mrtech.adminportal.controller;

import com.mrtech.adminportal.entity.courses;
import com.mrtech.adminportal.repository.coursesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/courses")
public class coursesController {

    @Autowired
    private coursesRepository courseRepo;

    // ✅ Add Course
    @PostMapping("/add")
    public ResponseEntity<courses> addCourse(@RequestBody courses course) {
        Long maxId = courseRepo.findMaxCourseId();
        course.setCourseid((maxId == null ? 0L : maxId) + 1);
        return ResponseEntity.ok(courseRepo.save(course));
    }

    // ✅ Get All Courses
    @GetMapping("/all")
    public ResponseEntity<List<courses>> getAllCourses() {
        return ResponseEntity.ok(courseRepo.findAll());
    }

    // ✅ Delete Course
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ✅ Get Course Names Only
    @GetMapping("/names")
    public ResponseEntity<List<String>> getCourseNames() {
        return ResponseEntity.ok(
                courseRepo.findAll()
                        .stream()
                        .map(courses::getCoursename)
                        .toList()
        );
    }
}
