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

  //  @GetMapping("/dashboard1")
  //  public String dashboard1(Model model, HttpSession session) {
   //     return "dashboard"; // refers to templates/dashboard.html
  //  }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", username); // This line makes it available in Thymeleaf
        return "dashboard"; // maps to src/main/resources/templates/dashboard.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // maps to templates/login.html
    }
}