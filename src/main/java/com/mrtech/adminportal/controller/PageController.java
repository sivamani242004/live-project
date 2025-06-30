package com.mrtech.adminportal.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import com.mrtech.adminportal.entity.Student;
import com.mrtech.adminportal.repository.StudentRepository;

@Controller
public class PageController {
	@Autowired
    private StudentRepository studentRepository;

	@GetMapping("/studenthome")
	public String studentHomePage(Model model) {
	    return "studenthome"; // loads studenthome.html
	}
	
	@GetMapping("/studentlist")
	public String studentlistPage(Model model) {
	    return "studentlist"; // loads studentlist.html
	}
	
    @GetMapping("/student")
    public String studentPage(Model model) {
        model.addAttribute("username", "Admin"); // Optional if you want to show username
        return "student"; // This will render student.html
    }
    
 
    @GetMapping("/student/edit/{id}")
    public String editStudentPage(@PathVariable int id, Model model) {
          model.addAttribute("studentId", id);
        return "student-edit";
    }
}