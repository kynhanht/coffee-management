package com.example.coffeemanagement.controller;

import com.example.coffeemanagement.dto.NhanVienDetailDTO;
import com.example.coffeemanagement.service.IChucVuService;
import com.example.coffeemanagement.service.INhanVienService;
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
    private final INhanVienService nhanVienService;
    private final IChucVuService chucVuService;
    public HomeController(IChucVuService chucVuService, INhanVienService nhanVienService) {
        this.chucVuService = chucVuService;
        this.nhanVienService = nhanVienService;
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
        String tenDangNhap = SecurityUtils.getPrincipal().getUsername();
        NhanVienDetailDTO detail = nhanVienService.getDetail(tenDangNhap);
        model.addAttribute("dsChucVu", chucVuService.getAll());
        model.addAttribute("employee", detail);
        model.addAttribute("title", "Trang cá nhân");
        model.addAttribute("content", "profile");
        return "layout/main";

    }
    @PostMapping("/profile/{tenDangNhap}")
    public String updateProfile(@ModelAttribute("employee") NhanVienDetailDTO dto,
                                @PathVariable("tenDangNhap") String tenDangNhap,
                                RedirectAttributes redirectAttributes){

        nhanVienService.updateProfile(tenDangNhap, dto);

        redirectAttributes.addFlashAttribute("success",
                "Cập nhật thành công!");
        return "redirect:/profile";
    }

} 