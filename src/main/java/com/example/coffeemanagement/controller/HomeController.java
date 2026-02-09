package com.example.coffeemanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/")
    public String index(Model model){
        model.addAttribute("title", "Trang chủ");
        model.addAttribute("content", "home");
        return "layout/main";
    }

    @RequestMapping("/admin")
    public String admin (){
        return "redirect:/";
    }


} 