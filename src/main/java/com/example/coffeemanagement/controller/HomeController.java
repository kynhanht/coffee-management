package com.example.coffeemanagement.controller;

import com.example.coffeemanagement.constant.SystemConstants;
import com.example.coffeemanagement.security.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class HomeController {
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("/admin")
    public String admin (){
        return "redirect:/";
    }

    private String determineTarget(Authentication authentication) {
        String target = "";
        // When not login or error
        if(!authentication.isAuthenticated()){
            target = "login";
        }
        // When login
        else{
            return "index";
        }
        return target;
    }



} 