package com.mrtech.adminportal.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mrtech.adminportal.entity.Login;
import com.mrtech.adminportal.repository.LoginRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private LoginRepository loginRepo;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login, HttpSession session) {
        Optional<Login> user = loginRepo.findByUsernameAndPassword(login.getUsername(), login.getPassword());

        if (user.isPresent()) {
            // ✅ Store username in session
            session.setAttribute("username", user.get().getUsername());

            return ResponseEntity.ok().body("success");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password..");
        }
    }
}
