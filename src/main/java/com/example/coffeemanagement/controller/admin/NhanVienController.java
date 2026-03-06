package com.example.coffeemanagement.controller.admin;

import com.example.coffeemanagement.dto.NhanVienDTO;
import com.example.coffeemanagement.dto.NhanVienListDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.service.IChucVuService;
import com.example.coffeemanagement.service.INhanVienService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/employees")
public class NhanVienController {

    private final INhanVienService nhanVienService;
    private final IChucVuService chucVuService;

    public NhanVienController(INhanVienService nhanVienService, IChucVuService chucVuService) {
        this.nhanVienService = nhanVienService;
        this.chucVuService = chucVuService;
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "5") int size,
                       @RequestParam(defaultValue = "HoTen") String sort,
                       @RequestParam(defaultValue = "asc") String dir,
                       @RequestParam(required = false) String searchValue,
                       Model model) {

        PageDTO<NhanVienListDTO> pageData =
                nhanVienService.getAll(page, size, sort, dir, searchValue);
        model.addAttribute("pageData", pageData);
        model.addAttribute("title", "Danh sách nhân viên");
        model.addAttribute("content", "admin/employee/list");
        return "layout/main";
    }


    @GetMapping("/addOrEdit")
    public String addOrEdit(@RequestParam(required = false) String maNhanVien, Model model) {
        NhanVienDTO dto;
        if(maNhanVien != null){
            dto = nhanVienService.getNhanVienById(maNhanVien);
            model.addAttribute("title", "Cập nhật nhân viên");
            model.addAttribute("action", "edit");
        }else{
            dto = new NhanVienDTO();
            model.addAttribute("title", "Thêm nhân viên");
            model.addAttribute("action", "add");
        }
        model.addAttribute("dsChucVu", chucVuService.getAll());
        model.addAttribute("employee",dto);
        model.addAttribute("content", "admin/employee/addOrEdit");
        return "layout/main";
    }

    @PostMapping("/add")
    public String createNhanVien(
            @ModelAttribute("employee") NhanVienDTO dto,
            RedirectAttributes redirectAttributes
    ) {
        nhanVienService.createNhanVien(dto);
        redirectAttributes.addFlashAttribute("success",
                "Thêm nhân viên thành công!");
        return "redirect:/admin/employees/list";
    }

    @PutMapping("/edit")
    public String editNhanVien(
            @RequestParam String maNhanVien,
            @ModelAttribute("employee") NhanVienDTO dto,
            RedirectAttributes redirectAttributes
    ) {
        nhanVienService.updateNhanVien(maNhanVien, dto);
        redirectAttributes.addFlashAttribute("success",
                "Cập nhập nhân viên thành công!");

        return "redirect:/admin/employees/list";
    }

    @DeleteMapping("/delete")
    public String deleteNhanVien(
            @RequestParam String maNhanVien,
            RedirectAttributes redirectAttributes
    ) {
        nhanVienService.deleteNhanVien(maNhanVien);
        redirectAttributes.addFlashAttribute("success",
                "Xóa nhân viên thành công!");

        return "redirect:/admin/employees/list";
    }
}