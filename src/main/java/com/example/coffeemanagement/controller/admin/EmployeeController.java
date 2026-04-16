package com.example.coffeemanagement.controller.admin;

import com.example.coffeemanagement.dto.EmployeeDTO;
import com.example.coffeemanagement.dto.EmployeeListDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.service.IEmployeeService;
import com.example.coffeemanagement.service.IPositionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/employees")
public class EmployeeController {

    private final IEmployeeService employeeService;
    private final IPositionService positionService;

    public EmployeeController(IEmployeeService employeeService, IPositionService positionService) {
        this.employeeService = employeeService;
        this.positionService = positionService;
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "5") int size,
                       @RequestParam(defaultValue = "HoTen") String sort,
                       @RequestParam(defaultValue = "asc") String dir,
                       @RequestParam(required = false) String searchValue,
                       Model model) {

        PageDTO<EmployeeListDTO> pageData =
                employeeService.getAllEmployees(page, size, sort, dir, searchValue);
        model.addAttribute("pageData", pageData);
        model.addAttribute("title", "Danh sách nhân viên");
        model.addAttribute("content", "admin/employee/list");
        return "layout/main";
    }


    @GetMapping("/addOrEdit")
    public String addOrEdit(@RequestParam(required = false) String employeeId, Model model) {
        EmployeeDTO dto;
        if(employeeId != null){
            dto = employeeService.getEmployee(employeeId);
            model.addAttribute("title", "Cập nhật nhân viên");
            model.addAttribute("action", "edit");
        }else{
            dto = new EmployeeDTO();
            model.addAttribute("title", "Thêm nhân viên");
            model.addAttribute("action", "add");
        }
        model.addAttribute("positionList", positionService.getAllPositions());
        model.addAttribute("employee",dto);
        model.addAttribute("content", "admin/employee/addOrEdit");
        return "layout/main";
    }

    @PostMapping
    public String add(
            @ModelAttribute("employee") EmployeeDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ");
            return "error/500";
        }
        employeeService.createEmployee(dto);
        redirectAttributes.addFlashAttribute("success",
                "Thêm nhân viên thành công!");
        return "redirect:/admin/employees/list";
    }

    @PutMapping
    public String edit(
            @RequestParam String id,
            @ModelAttribute("employee") EmployeeDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ");
            return "error/500";
        }
        employeeService.updateEmployee(id, dto);
        redirectAttributes.addFlashAttribute("success",
                "Cập nhập nhân viên thành công!");

        return "redirect:/admin/employees/list";
    }

    @DeleteMapping
    public String delete(
            @RequestParam String id,
            RedirectAttributes redirectAttributes
    ) {
        employeeService.deleteEmployee(id);
        redirectAttributes.addFlashAttribute("success",
                "Xóa nhân viên thành công!");

        return "redirect:/admin/employees/list";
    }

}