package com.example.coffeemanagement.controller;

import com.example.coffeemanagement.dto.EmployeeDetailDTO;
import com.example.coffeemanagement.service.IEmployeeService;
import com.example.coffeemanagement.service.IPositionService;
import com.example.coffeemanagement.util.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {
    private final IEmployeeService employeeService;
    private final IPositionService positionService;

    public HomeController(IEmployeeService employeeService, IPositionService positionService) {
        this.employeeService = employeeService;
        this.positionService = positionService;
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
    @GetMapping("/")
    public String index(Model model){
        model.addAttribute("title", "Trang chủ");
        model.addAttribute("content", "home");
        return "layout/main";
    }

    @GetMapping("/admin")
    public String admin (){
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profile(Model model){
        String username = SecurityUtils.getPrincipal().getUsername();
        EmployeeDetailDTO detail = employeeService.getDetail(username);
        model.addAttribute("positionList", positionService.getAll());
        model.addAttribute("employee", detail);
        model.addAttribute("title", "Trang cá nhân");
        model.addAttribute("content", "profile");
        return "layout/main";

    }
    @PostMapping("/profile/{username}")
    public String updateProfile(@ModelAttribute("employee") EmployeeDetailDTO dto,
                                @PathVariable String username,
                                RedirectAttributes redirectAttributes){

        employeeService.updateProfile(username, dto);

        redirectAttributes.addFlashAttribute("success",
                "Cập nhật thành công!");
        return "redirect:/profile";
    }

} 