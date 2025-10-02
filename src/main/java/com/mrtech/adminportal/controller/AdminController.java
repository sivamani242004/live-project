package com.mrtech.adminportal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    // Loads templates/admin/index.html
    @GetMapping("/admin")
    public String adminPage() {
        return "admin/index";
    }
}
