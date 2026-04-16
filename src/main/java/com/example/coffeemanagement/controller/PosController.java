package com.example.coffeemanagement.controller;

import com.example.coffeemanagement.dto.OrderDTO;
import com.example.coffeemanagement.dto.OrderItemSelectDTO;
import com.example.coffeemanagement.dto.TableDTO;
import com.example.coffeemanagement.dto.TableOptionDTO;
import com.example.coffeemanagement.dto.request.*;
import com.example.coffeemanagement.dto.response.*;
import com.example.coffeemanagement.enums.TableStatus;
import com.example.coffeemanagement.service.IOrderItemService;
import com.example.coffeemanagement.service.IOrderService;
import com.example.coffeemanagement.service.ITableService;
import com.example.coffeemanagement.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/pos")
public class PosController {

    private final ITableService tableService;
    private final IOrderService orderService;
    private final IOrderItemService orderItemService;

    public PosController(ITableService tableService, IOrderService orderService, IOrderItemService orderItemService) {
        this.tableService = tableService;
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public String pos(Model model) {
        List<TableDTO> listTable = tableService.getAllTables();
        model.addAttribute("tables", listTable);
        model.addAttribute("title", "Quản lý bán hàng");
        model.addAttribute("content", "pos");
        return "layout/main";
    }

    @GetMapping("/view")
    public String displayViewTable(@RequestParam String sourceTableId, RedirectAttributes redirectAttributes) {
        ViewTableResponse viewTableResponse = new ViewTableResponse();
        viewTableResponse.setTableInfo(tableService.getTableInfo(sourceTableId));
        viewTableResponse.setOrderItemList(orderItemService.getOrderItemsForTable(sourceTableId));

        redirectAttributes.addFlashAttribute("viewTableResponse", viewTableResponse);
        redirectAttributes.addFlashAttribute("modal", "viewTableModal");
        return "redirect:/pos";
    }

    @GetMapping("/reserve")
    public String displayReserveTable(@RequestParam String sourceTableId, RedirectAttributes redirectAttributes) {
        ReserveTableRequest reserveTableRequest = new ReserveTableRequest();
        reserveTableRequest.setSourceTableId(sourceTableId);
        redirectAttributes.addFlashAttribute("modal", "reserveTableModal");
        redirectAttributes.addFlashAttribute("reserveTableRequest", reserveTableRequest);
        return "redirect:/pos";
    }

    @PostMapping("/reserve")
    public String reserveTable(@Valid @ModelAttribute(name = "reserveTableRequest") ReserveTableRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ");
            return "error/500";
        }
        String employeeId = SecurityUtils.getPrincipal().getEmployeeEntity().getId();
        request.setEmployeeId(employeeId);

        tableService.reserveTable(request);
        redirectAttributes.addFlashAttribute("success", "đặt bàn thành công");
        return "redirect:/pos";
    }

    @GetMapping("/order")
    public String displayOrderTable(@RequestParam String sourceTableId, RedirectAttributes redirectAttributes) {
        // Response
        TableDTO sourceTable = tableService.getTable(sourceTableId);
        OrderTableResponse orderTableResponse = new OrderTableResponse();
        List<OrderItemSelectDTO> orderItemList = orderItemService.getMenuWithOrderItemsForTable(sourceTableId);
        orderTableResponse.setSourceTable(sourceTable);
        orderTableResponse.setOrderItemList(orderItemList);

        // Request
        OrderTableRequest orderTableRequest = new OrderTableRequest();
        orderTableRequest.setSourceTableId(sourceTableId);
        orderTableRequest.setOrderItemList(orderItemList);

        redirectAttributes.addFlashAttribute("modal", "orderTableModal");
        redirectAttributes.addFlashAttribute("orderTableRequest", orderTableRequest);
        redirectAttributes.addFlashAttribute("orderTableResponse", orderTableResponse);
        return "redirect:/pos";
    }

    @PostMapping("/order")
    public String orderTable(@Valid @ModelAttribute(name = "orderTableRequest") OrderTableRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ");
            return "error/500";
        }
        String employeeId = SecurityUtils.getPrincipal().getEmployeeEntity().getId();
        request.setEmployeeId(employeeId);
        orderService.saveOrder(request);
        redirectAttributes.addFlashAttribute("success", "Đặt đơn thành công");
        return "redirect:/pos";
    }

    @GetMapping("/move")
    public String displayMoveTable(@RequestParam String sourceTableId, RedirectAttributes redirectAttributes) {
        TableDTO sourceTable = tableService.getTable(sourceTableId);
        List<TableOptionDTO> tableList = tableService.getSelectableTables(sourceTableId, List.of(TableStatus.AVAILABLE));
        MoveTableResponse moveTableResponse = new MoveTableResponse(sourceTable, tableList);
        MoveTableRequest moveTableRequest = new MoveTableRequest();
        moveTableRequest.setSourceTableId(sourceTable.getId());

        redirectAttributes.addFlashAttribute("modal", "moveTableModal");
        redirectAttributes.addFlashAttribute("moveTableRequest", moveTableRequest);
        redirectAttributes.addFlashAttribute("moveTableResponse", moveTableResponse);
        return "redirect:/pos";
    }

    @PostMapping("/move")
    public String moveTable(@Valid @ModelAttribute(name = "moveTableRequest") MoveTableRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ");
            return "error/500";
        }
        tableService.moveTable(request);
        redirectAttributes.addFlashAttribute("success", "Chuyển bàn thành công");
        return "redirect:/pos";
    }

    @GetMapping("/merge")
    public String displayMergeTable(@RequestParam String sourceTableId, RedirectAttributes redirectAttributes) {

        TableDTO sourceTable = tableService.getTable(sourceTableId);
        List<TableOptionDTO> mergeableSourceTableList = tableService.getSelectableTables(sourceTableId, List.of(TableStatus.OCCUPIED));
        List<TableOptionDTO> mergeableTargetTableList = tableService.getSelectableTables(sourceTableId, List.of(TableStatus.AVAILABLE, TableStatus.OCCUPIED));

        MergeTableResponse mergeTableResponse = new MergeTableResponse(sourceTable, mergeableSourceTableList, mergeableTargetTableList);
        MergeTableRequest mergeTableRequest = new MergeTableRequest();
        mergeTableRequest.setSourceTableId(sourceTableId);
        mergeTableRequest.setMergeableSourceTableList(mergeableSourceTableList);
        redirectAttributes.addFlashAttribute("modal", "mergeTableModal");
        redirectAttributes.addFlashAttribute("mergeTableResponse", mergeTableResponse);
        redirectAttributes.addFlashAttribute("mergeTableRequest", mergeTableRequest);
        return "redirect:/pos";
    }

    @PostMapping("/merge")
    public String mergeTable(@Valid @ModelAttribute(name = "mergeTableRequest") MergeTableRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ");
            return "error/500";
        }
        String employeeId = SecurityUtils.getPrincipal().getEmployeeEntity().getId();
        request.setEmployeeId(employeeId);
        tableService.mergeTables(request);
        redirectAttributes.addFlashAttribute("success", "Gộp bàn thành công");
        return "redirect:/pos";
    }

    @GetMapping("/split")
    public String displaySplitTable(@RequestParam String sourceTableId, @RequestParam(required = false) String targetTableId, RedirectAttributes redirectAttributes) {
        TableDTO sourceTable = tableService.getTable(sourceTableId);
        List<OrderItemSelectDTO> sourceOrderMenuItemList = orderItemService.getOrderItemsForTable(sourceTableId);
        List<TableOptionDTO> selectableTableList = tableService.getSelectableTables(sourceTableId, List.of(TableStatus.AVAILABLE, TableStatus.OCCUPIED));
        TableDTO targetTable;
        List<OrderItemSelectDTO> targetOrderMenuItemList;
        if (targetTableId == null || targetTableId.isBlank()) {
            targetTable = new TableDTO();
            targetOrderMenuItemList = new ArrayList<>();
        } else {
            targetTable = tableService.getTable(targetTableId);
            targetOrderMenuItemList = orderItemService.getOrderItemsForTable(targetTableId);
        }
        SplitTableResponse splitTableResponse = new SplitTableResponse();
        splitTableResponse.setSourceTable(sourceTable);
        splitTableResponse.setTargetTable(targetTable);
        splitTableResponse.setSelectableTableList(selectableTableList);
        splitTableResponse.setSourceOrderItemList(sourceOrderMenuItemList);
        splitTableResponse.setTargetOrderItemList(targetOrderMenuItemList);

        SplitTableRequest splitTableRequest = new SplitTableRequest();
        splitTableRequest.setSourceTableId(sourceTableId);
        splitTableRequest.setTargetTableId(targetTableId);
        splitTableRequest.setSplitOrderItemList(sourceOrderMenuItemList);


        redirectAttributes.addFlashAttribute("modal", "splitTableModal");
        redirectAttributes.addFlashAttribute("splitTableResponse", splitTableResponse);
        redirectAttributes.addFlashAttribute("splitTableRequest", splitTableRequest);
        return "redirect:/pos";
    }

    @PostMapping("/split")
    public String splitTable(@Valid @ModelAttribute(name = "splitTableRequest") SplitTableRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ");
            return "error/500";
        }
        String employeeId = SecurityUtils.getPrincipal().getEmployeeEntity().getId();
        request.setEmployeeId(employeeId);
        tableService.splitTable(request);
        redirectAttributes.addFlashAttribute("success", "Tách bàn thành công");
        return "redirect:/pos";
    }

    @GetMapping("/cancel")
    public String displaySplitTable(@RequestParam String sourceTableId, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("sourceTableId", sourceTableId);
        redirectAttributes.addFlashAttribute("modal", "cancelTableModal");
        return "redirect:/pos";
    }

    @PostMapping("/cancel")
    public String cancelTable(@RequestParam String sourceTableId, RedirectAttributes redirectAttributes){
        tableService.cancelTable(sourceTableId);
        redirectAttributes.addFlashAttribute("success", "Hủy bàn thành công");
        return "redirect:/pos";
    }

    @GetMapping("/pay")
    public String displayPayTable(@RequestParam String sourceTableId, RedirectAttributes redirectAttributes) {
        TableDTO sourceTable = tableService.getTable(sourceTableId);
        PayOrderResponse payOrderResponse = new PayOrderResponse();
        payOrderResponse.setSourceTable(sourceTable);
        payOrderResponse.setOrderItemList(orderItemService.getOrderItemsForTable(sourceTableId));
        String orderId = orderService.getUnpaidOrderId(sourceTableId);
        String totalAmount = orderService.getTotalAmount(orderId);
        payOrderResponse.setTotalAmount(totalAmount);


        PayOrderRequest payOrderRequest = new PayOrderRequest();
        payOrderRequest.setSourceTableId(sourceTable.getId());
        payOrderRequest.setOrderId(orderId);
        payOrderRequest.setTotalAmount(totalAmount);


        redirectAttributes.addFlashAttribute("payOrderRequest", payOrderRequest);
        redirectAttributes.addFlashAttribute("payOrderResponse", payOrderResponse);
        redirectAttributes.addFlashAttribute("modal", "payOrderModal");
        return "redirect:/pos";
    }

    @PostMapping("/pay")
    public String payOrder(@Valid @ModelAttribute(name = "payOrderRequest") PayOrderRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Dữ liệu không hợp lệ");
            return "error/500";
        }
        orderService.payOrder(request);
        redirectAttributes.addAttribute("orderId", request.getOrderId());
        return "redirect:/pos/invoice";
    }

    @GetMapping("/invoice")
    public String printInvoice(@RequestParam String orderId, Model model){
        OrderDTO order = orderService.getOrder(orderId);
        model.addAttribute("order", order);
        return "invoice";
    }


}
