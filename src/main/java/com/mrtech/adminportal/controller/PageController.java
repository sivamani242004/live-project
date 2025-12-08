package com.mrtech.adminportal.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.mrtech.adminportal.service.StudentService;

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
	@GetMapping("/courses")
	public String CoursesPage(Model model) {
	    return "courses"; // loads studenthome.html
	}
	@GetMapping("/Batch")
	public String BatchPage(Model model) {
	    return "Batch"; // loads studenthome.html
	}
	@GetMapping("/reports")
	public String reportsPage(Model model) {
	    return "reports"; // loads studenthome.html
	}
	
	
    @GetMapping("/student")
    public String studentPage(Model model) {
        model.addAttribute("username", "Admin"); // Optional if you want to show username
        return "student"; // This will render student.html
    }
    @GetMapping("/payments")
    public String paymentPage(Model model) {
        model.addAttribute("username", "Admin"); // Optional if you want to show username
        return "payments"; // This will render payments.html
    }
    
 
    @GetMapping("/student/edit/{id}")
    public String editStudentPage(@PathVariable int id, Model model) {
          model.addAttribute("studentId", id);
        return "student-edit";
    }
    @Autowired
    private StudentService studentService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalStudents = studentService.getTotalStudents();
        model.addAttribute("totalStudents", totalStudents);
        return "dashboard"; // This should match your dashboard.html filename
    }
    @GetMapping("/studentlist")
    public String studentlistpage(Model model) {
        long totalStudents = studentService.getTotalStudents();
        model.addAttribute("totalStudents", totalStudents);
        return "studentlist"; // This should match your studentlist.html filename
    }
}