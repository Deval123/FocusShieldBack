package com.dev.focusshield.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/hello")
    public ResponseEntity<String> helloAdmin() {
        return ResponseEntity.ok("Hello Admin!");
    }
}