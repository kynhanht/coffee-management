package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IMenuItemDAO;
import com.example.coffeemanagement.dao.IOrderItemDAO;
import com.example.coffeemanagement.dao.ITableDAO;
import com.example.coffeemanagement.dto.OrderItemDTO;
import com.example.coffeemanagement.dto.OrderItemSelectDTO;
import com.example.coffeemanagement.entity.MenuItemEntity;
import com.example.coffeemanagement.entity.TableEntity;
import com.example.coffeemanagement.enums.OrderStatus;
import com.example.coffeemanagement.enums.RecordStatus;
import com.example.coffeemanagement.enums.TableStatus;
import com.example.coffeemanagement.exception.NotFoundException;
import com.example.coffeemanagement.service.IOrderItemService;
import com.example.coffeemanagement.util.SystemUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderItemService implements IOrderItemService {


    private final IOrderItemDAO orderItemDAO;
    private final IMenuItemDAO menuItemDAO;
    private final ITableDAO tableDAO;

    public OrderItemService(IMenuItemDAO menuItemDAO, IOrderItemDAO orderItemDAO, ITableDAO tableDAO) {
        this.menuItemDAO = menuItemDAO;
        this.orderItemDAO = orderItemDAO;
        this.tableDAO = tableDAO;
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderItemSelectDTO> getOrderItemsForTable(String tableId) {
        return orderItemDAO.findByTableIdAndOrderStatus(tableId, OrderStatus.UNPAID.name()).stream()
                .map(orderItem -> new OrderItemSelectDTO(orderItem.getMenuItemId(), orderItem.getMenuItemName(), orderItem.getQuantity(), orderItem.getCurrentPrice(), orderItem.getLineTotal(), false))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderItemSelectDTO> getMenuWithOrderItemsForTable(String tableId) {

        TableEntity tableEntity = tableDAO.findById(tableId)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.TABLE_NOT_FOUND + ": " + tableId));


        // 1. Lấy toàn bộ danh sách món ăn có sẵn
        List<MenuItemEntity> menuItemList = menuItemDAO.findByStatus(RecordStatus.ACTIVE.name());

        List<OrderItemSelectDTO> result;

        // Trạng thái [Đang phục vụ] => tức là có các món ăn đã gọi
        if (tableEntity.getStatus().equals(TableStatus.OCCUPIED.name())) {
            // 2. Lấy danh sách món ăn đã gọi(Lấy ra các món ăn của từng bàn => lấy theo id của từng mà bàn và trạng thái hóa đơn là chưa thanh toán)
            List<OrderItemDTO> menuItemOrderList = orderItemDAO.findByTableIdAndOrderStatus(tableId, OrderStatus.UNPAID.name());
            // 3. Gộp lại, nếu không có trong order thì soLuong = 0
            Map<String, OrderItemDTO> orderMap = menuItemOrderList.stream()
                    .collect(Collectors.toMap(OrderItemDTO::getMenuItemId, o -> o));

            result = menuItemList.stream()
                    .map(menuItem -> {
                        OrderItemDTO orderItemDTO = orderMap.get(menuItem.getId());

                        return new OrderItemSelectDTO(
                                menuItem.getId(),
                                menuItem.getName(),
                                orderItemDTO != null ? orderItemDTO.getQuantity() : 0,
                                menuItem.getPrice(),
                                orderItemDTO != null ? menuItem.getPrice().multiply(BigDecimal.valueOf(orderItemDTO.getQuantity())) : null,
                                orderItemDTO != null
                        );
                    })
                    .toList();
        } else { // Trạng thái còn lại [Đặt bàn] và [Đặt trước] => Không có món ăn nào được gọi
            // 2.  Không có trong order thì soLuong = 0
            result = menuItemList.stream()
                    .map(menuItem -> new OrderItemSelectDTO(menuItem.getId(), menuItem.getName(), 0, menuItem.getPrice(), null, false))
                    .toList();
        }
        return result;
    }
}
