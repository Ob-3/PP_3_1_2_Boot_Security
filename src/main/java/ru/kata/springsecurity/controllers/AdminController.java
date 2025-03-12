package ru.kata.springsecurity.controllers;

import ru.kata.springsecurity.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/home")
    public String adminHome(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin-home";
    }
}

