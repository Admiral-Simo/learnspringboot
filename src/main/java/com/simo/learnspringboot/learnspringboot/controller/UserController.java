package com.simo.learnspringboot.learnspringboot.controller;

import com.simo.learnspringboot.learnspringboot.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    @GetMapping("/register")
    public String showForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String submitForm(@ModelAttribute User user, Model model) {
        model.addAttribute("message", "User registered successfully: " + user.getEmail());
        return "result";
    }
}
