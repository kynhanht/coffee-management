package com.example.coffeemanagement.controller;

import com.example.coffeemanagement.dto.EmployeeDTO;
import com.example.coffeemanagement.dto.request.EmployeeProfileRequest;
import com.example.coffeemanagement.dto.response.EmployeeProfileResponse;
import com.example.coffeemanagement.service.IEmployeeService;
import com.example.coffeemanagement.util.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {
    private final IEmployeeService employeeService;

    public HomeController(IEmployeeService employeeService) {
        this.employeeService = employeeService;
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
        String employeeId = SecurityUtils.getPrincipal().getEmployeeEntity().getId();
        // Response
        EmployeeProfileResponse employeeProfileResponse = employeeService.getProfile(employeeId);
        // Request
        EmployeeDTO employeeDTO = employeeService.getEmployee(employeeId);
        EmployeeProfileRequest employeeProfileRequest = new EmployeeProfileRequest();
        employeeProfileRequest.setEmployeeId(employeeId);
        employeeProfileRequest.setFullName(employeeDTO.getFullName());
        employeeProfileRequest.setAddress(employeeDTO.getAddress());
        employeeProfileRequest.setPhone(employeeDTO.getPhone());
        employeeProfileRequest.setPicture(employeeDTO.getPicture());

        model.addAttribute("employeeProfileResponse", employeeProfileResponse);
        model.addAttribute("employeeProfileRequest", employeeProfileRequest);
        model.addAttribute("title", "Trang cá nhân");
        model.addAttribute("content", "profile");
        return "layout/main";

    }
    @PutMapping("/profile")
    public String updateProfile(
                                @RequestParam String employeeId,
                                @ModelAttribute("employeeProfileRequest") EmployeeProfileRequest request,
                                RedirectAttributes redirectAttributes){

        employeeService.updateProfile(employeeId, request);
        redirectAttributes.addFlashAttribute("success",
                "Cập nhật thành công!");
        return "redirect:/profile";
    }

} 