package com.example.coffeemanagement.controller.admin;

import com.example.coffeemanagement.dto.DeviceDTO;
import com.example.coffeemanagement.dto.DeviceListDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.service.IDeviceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/devices")
public class DeviceController {

    private final IDeviceService deviceService;

    public DeviceController(IDeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "5") int size,
                       @RequestParam(defaultValue = "TenThietBi") String sort,
                       @RequestParam(defaultValue = "asc") String dir,
                       @RequestParam(required = false) String searchValue,
                       Model model) {

        PageDTO<DeviceListDTO> pageData =
                deviceService.getAllDevices(page, size, sort, dir, searchValue);
        model.addAttribute("pageData", pageData);
        model.addAttribute("title", "Danh sách thiết bị");
        model.addAttribute("content", "admin/device/list");
        return "layout/main";
    }

    @GetMapping("/addOrEdit")
    public String addOrEdit(@RequestParam(required = false) String deviceId, Model model) {
        DeviceDTO dto;
        if (deviceId != null) {
            dto = deviceService.getDevice(deviceId);
            model.addAttribute("title", "Cập nhật thiết bị");
            model.addAttribute("action", "edit");
        } else {
            dto = new DeviceDTO();
            model.addAttribute("title", "Thêm thiết bị");
            model.addAttribute("action", "add");
        }
        model.addAttribute("device", dto);
        model.addAttribute("content", "admin/device/addOrEdit");
        return "layout/main";
    }

    @PostMapping
    public String add(
            @ModelAttribute("device") DeviceDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ");
            return "error/500";
        }
        deviceService.createDevice(dto);
        redirectAttributes.addFlashAttribute("success",
                "Thêm thiết bị thành công!");
        return "redirect:/admin/devices/list";
    }

    @PutMapping
    public String edit(
            @RequestParam String id,
            @ModelAttribute("device") DeviceDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ");
            return "error/500";
        }
        deviceService.updateDevice(id, dto);
        redirectAttributes.addFlashAttribute("success",
                "Cập nhập thiết bị thành công!");

        return "redirect:/admin/devices/list";
    }

    @DeleteMapping
    public String delete(
            @RequestParam String id,
            RedirectAttributes redirectAttributes
    ) {
        deviceService.deleteDevice(id);
        redirectAttributes.addFlashAttribute("success",
                "Xóa thiết bị thành công!");

        return "redirect:/admin/devices/list";
    }
}
