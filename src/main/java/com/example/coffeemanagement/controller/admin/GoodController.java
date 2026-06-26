package com.example.coffeemanagement.controller.admin;

import com.example.coffeemanagement.dto.GoodDTO;
import com.example.coffeemanagement.dto.GoodListDTO;
import com.example.coffeemanagement.dto.ImportExportGoodDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.service.IGoodService;
import com.example.coffeemanagement.service.IUnitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/goods")
public class GoodController {
    private final IGoodService goodService;
    private final IUnitService unitService;

    public GoodController(IGoodService goodService, IUnitService unitService) {
        this.goodService = goodService;
        this.unitService = unitService;
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "5") int size,
                       @RequestParam(defaultValue = "TenHangHoa") String sort,
                       @RequestParam(defaultValue = "asc") String dir,
                       @RequestParam(required = false) String searchValue,
                       Model model) {

        PageDTO<GoodListDTO> pageData =
                goodService.getAllGoods(page, size, sort, dir, searchValue);
        model.addAttribute("pageData", pageData);
        model.addAttribute("title", "Danh sách hàng hóa");
        model.addAttribute("content", "admin/good/list");
        return "layout/main";
    }

    @GetMapping("/addOrEdit")
    public String addOrEdit(@RequestParam(required = false) String goodId, Model model) {
        GoodDTO dto;
        if (goodId != null) {
            dto = goodService.getGood(goodId);
            model.addAttribute("title", "Chỉnh sửa hàng hóa");
            model.addAttribute("action", "edit");
        } else {
            dto = new GoodDTO();
            model.addAttribute("title", "Thêm hàng hóa");
            model.addAttribute("action", "add");
        }

        model.addAttribute("unitList", unitService.getAllUnits());

        model.addAttribute("good", dto);
        model.addAttribute("content", "admin/good/addOrEdit");
        return "layout/main";
    }

    @PostMapping
    public String add(
            @ModelAttribute("good") GoodDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ");
            return "error/500";
        }
        goodService.createGood(dto);
        redirectAttributes.addFlashAttribute("success",
                "Thêm hàng hóa thành công!");
        return "redirect:/admin/goods/list";
    }

    @PutMapping
    public String edit(
            @RequestParam String id,
            @ModelAttribute("good") GoodDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ");
            return "error/500";
        }
        goodService.updateGood(id, dto);
        redirectAttributes.addFlashAttribute("success",
                "Cập nhập hàng hóa thành công!");

        return "redirect:/admin/goods/list";
    }

    @DeleteMapping
    public String delete(
            @RequestParam String id,
            RedirectAttributes redirectAttributes
    ) {
        goodService.deleteGood(id);
        redirectAttributes.addFlashAttribute("success",
                "Xóa hàng hóa thành công!");

        return "redirect:/admin/goods/list";
    }


    @GetMapping("/import")
    public String importGood(Model model) {
        model.addAttribute("title", "Nhập hàng hóa");
        model.addAttribute("action", "import");
        ImportExportGoodDTO importExportGoodDTO = new ImportExportGoodDTO();
        model.addAttribute("goodList", goodService.getAllGoods());
        model.addAttribute("importExportGood", importExportGoodDTO);
        model.addAttribute("content", "admin/good/importOrExport");
        return "layout/main";
    }


    @GetMapping("/export")
    public String exportGood(Model model) {
        model.addAttribute("title", "Xuất hàng hóa");
        model.addAttribute("action", "export");
        ImportExportGoodDTO importExportGoodDTO = new ImportExportGoodDTO();
        model.addAttribute("goodList", goodService.getAllGoods());
        model.addAttribute("importExportGood", importExportGoodDTO);
        model.addAttribute("content", "admin/good/importOrExport");
        return "layout/main";
    }

    @PutMapping("/importOrExport")
    public String exportGood(
            @RequestParam String goodId,
            @ModelAttribute("good") ImportExportGoodDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ");
            return "error/500";
        }
        goodService.importOrExportGood(goodId, dto);
        redirectAttributes.addFlashAttribute("success",
                "Cập nhập hàng hóa thành công!");

        return "redirect:/admin/goods/list";
    }

}
