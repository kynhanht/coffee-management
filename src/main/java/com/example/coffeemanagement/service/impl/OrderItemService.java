package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IMenuItemDAO;
import com.example.coffeemanagement.dao.IOrderItemDAO;
import com.example.coffeemanagement.dao.ITableDAO;
import com.example.coffeemanagement.dto.MenuItemDTO;
import com.example.coffeemanagement.dto.OrderMenuItemDTO;
import com.example.coffeemanagement.dto.TableDTO;
import com.example.coffeemanagement.enums.RecordStatus;
import com.example.coffeemanagement.enums.TableStatus;
import com.example.coffeemanagement.exception.NotFoundException;
import com.example.coffeemanagement.service.IOrderItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderItemService implements IOrderItemService {
    private final IMenuItemDAO menuItemDAO;
    private final IOrderItemDAO orderItemDAO;
    private final ITableDAO tableDAO;

    public OrderItemService(IMenuItemDAO menuItemDAO, IOrderItemDAO orderItemDAO, ITableDAO tableDAO) {
        this.menuItemDAO = menuItemDAO;
        this.orderItemDAO = orderItemDAO;
        this.tableDAO = tableDAO;
    }

    @Override
    public List<OrderMenuItemDTO> getOrderByTableId(String tableId) {
        return orderItemDAO.findOrderByTableId(tableId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderMenuItemDTO> getMenuWithOrderByTableId(String tableId) {

        TableDTO tableDTO = tableDAO.findById(tableId)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.TABLE_NOT_FOUND + ": " + tableId));

        // 1. Lấy toàn bộ danh sách món ăn có sẵn
        List<MenuItemDTO> menuItemList = menuItemDAO.findByStatus(RecordStatus.ACTIVE.name());

        List<OrderMenuItemDTO> result;

        // Trạng thái [Đang phục vụ] => tức là có các món ăn đã gọi
        if (tableDTO.getStatus().equals(TableStatus.OCCUPIED)) {
            // 2. Lấy danh sách món ăn đã gọi(Lấy ra các món ăn của từng bàn => lấy theo id của từng mà bàn và trạng thái hóa đơn là chưa thanh toán)
            List<OrderMenuItemDTO> menuItemOrderList = orderItemDAO.findOrderByTableId(tableId);
            // 3. Gộp lại, nếu không có trong order thì soLuong = 0
            Map<String, OrderMenuItemDTO> orderMap = menuItemOrderList.stream()
                    .collect(Collectors.toMap(OrderMenuItemDTO::getId, o -> o));
            result = menuItemList.stream()
                    .map(menuItem -> {
                        OrderMenuItemDTO order = orderMap.get(menuItem.getId());
                        return new OrderMenuItemDTO(
                                menuItem.getId(),
                                menuItem.getName(),
                                order != null ? order.getQuantity() : 0,
                                menuItem.getPrice(),
                                order != null
                        );
                    })
                    .toList();
        } else { // Trạng thái còn lại [Đặt bàn] và [Đặt trước] => Không có món ăn nào được gọi
            // 2.  Không có trong order thì soLuong = 0
            result = menuItemList.stream()
                    .map(menuItem ->
                            new OrderMenuItemDTO(menuItem.getId(), menuItem.getName(), 0, menuItem.getPrice(), false)
                    )
                    .toList();
        }
        return result;
    }
}
