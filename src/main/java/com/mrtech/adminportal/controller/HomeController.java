package com.mrtech.adminportal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home"; // maps to templates/home.html
    }


    @GetMapping("/dashboard1")
    public String dashboard(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", username); // For Thymeleaf
        return "dashboard"; // maps to dashboard.html
    }

        
    
    
    @GetMapping("/login")
    public String login() {
        return "login"; // maps to templates/login.html
    }
}