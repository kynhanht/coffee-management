package com.example.coffeemanagement.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class AdminController {
    @RequestMapping("/admin")
    public String admin(){

        return "admin/index";
    }
}
