package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.*;
import com.example.coffeemanagement.dto.OrderDTO;
import com.example.coffeemanagement.dto.OrderItemDTO;
import com.example.coffeemanagement.dto.OrderItemSelectDTO;
import com.example.coffeemanagement.dto.request.OrderTableRequest;
import com.example.coffeemanagement.dto.request.PayOrderRequest;
import com.example.coffeemanagement.entity.*;
import com.example.coffeemanagement.enums.OrderStatus;
import com.example.coffeemanagement.enums.TableStatus;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.exception.NotFoundException;
import com.example.coffeemanagement.service.IOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService implements IOrderService {

    private final IOrderDAO orderDAO;
    private final IOrderItemDAO orderItemDAO;
    private final IMenuItemDAO menuItemDAO;
    private final ITableDAO tableDAO;
    private final IReservationDetailDAO reservationDetailDAO;

    public OrderService(IOrderDAO orderDAO, IOrderItemDAO orderItemDAO, IMenuItemDAO menuItemDAO, ITableDAO tableDAO, IReservationDetailDAO reservationDetailDAO) {
        this.orderDAO = orderDAO;
        this.orderItemDAO = orderItemDAO;
        this.menuItemDAO = menuItemDAO;
        this.tableDAO = tableDAO;
        this.reservationDetailDAO = reservationDetailDAO;
    }

    @Transactional(readOnly = true)
    @Override
    public String getUnpaidOrderId(String tableId) {
        return orderDAO.findOrderIdByTableIdAndStatus(tableId, OrderStatus.UNPAID.name())
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.UNPAID_ORDER_NOT_FOUND + ": " + tableId));
    }

    @Override
    public BigDecimal getTotalAmount(String id) {
        return orderDAO.findTotalAmountById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.ORDER_NOT_FOUND + ": " + id));
    }

    @Transactional
    @Override
    public void saveOrder(OrderTableRequest request) {
        // Lọc ra các món ăn được chọn
        List<OrderItemSelectDTO> orderItemList = request
                .getOrderItemList().stream()
                .filter(OrderItemSelectDTO::getSelected)
                .toList();

        if (orderItemList.isEmpty()) {
            throw new InternalException("Không có món ăn nào được gọi");
        }
        String tableId = request.getSourceTableId();
        // Lấy thông tin bàn hiên tại
        TableEntity table = tableDAO.findById(tableId)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.TABLE_NOT_FOUND + ": " + tableId));
        String orderId;
        // TH1: Trạng thái bàn AVAILABLE hoặc RESERVED => chưa tạo hóa đơn
        if (table.getStatus().equals(TableStatus.AVAILABLE.name()) || table.getStatus().equals(TableStatus.RESERVED.name())) {
            // 1. Tạo chi tiết đặt bàn => đối với trường hợp bàn trống
            if (table.getStatus().equals(TableStatus.AVAILABLE.name())) {
                ReservationDetailEntity reservationDetailEntity = new ReservationDetailEntity();
                reservationDetailEntity.setTableId(tableId);
                reservationDetailDAO.insert(new ReservationDetailEntity(tableId, request.getEmployeeId(), request.getCustomerName(), request.getCustomerPhone(), request.getReservationDate()));
            }
            // 2. Tạo mới hóa đơn
            orderId = orderDAO.generateNextId();
            orderDAO.insert(new OrderEntity(orderId, tableId, request.getEmployeeId(), null, request.getCustomerName(), request.getCustomerPhone(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, LocalDateTime.now(), OrderStatus.UNPAID.name()));
            // 3. Tạo mới chi tiết đặt bàn-
            orderItemList
                    .forEach(menuItem -> {
                        BigDecimal price = menuItemDAO.findById(menuItem.getMenuItemId())
                                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.MENU_ITEM_NOT_FOUND + ": " + menuItem.getMenuItemId()))
                                .getPrice();
                        orderItemDAO.insert(new OrderItemEntity(orderId, menuItem.getMenuItemId(), menuItem.getQuantity(), price));
                    });
            // 4. Cập nhập trạng thái bàn => OCCUPIED
            tableDAO.updateStatusById(tableId, TableStatus.OCCUPIED.name());
        }else{ // TH2: Bàn đang được sử dụng => đã có hóa đơn
            // Lấy các món ăn đã gọi của bàn đích
            List<String> menuItemIdList = orderItemDAO.findByTableIdAndOrderStatus(tableId, OrderStatus.UNPAID.name())
                    .stream()
                    .map(OrderItemDTO::getMenuItemId).toList();
            // Lấy hóa đơn đang sử dụng(Trạng thái: UNPAID)
            orderId = orderDAO.findOrderIdByTableIdAndStatus(tableId, OrderStatus.UNPAID.name())
                            .orElseThrow(()  -> new NotFoundException(ErrorMessageConstants.TABLE_NOT_FOUND + ": " + tableId));
            // 1. Chỉnh sửa chi tiết hóa đơn (nếu món ăn vừa gọi không trùng với món ăn đã có trong hóa đơn  => INSERT, Ngược lại => UPDATE số lượng)
            orderItemList
                    .forEach(menuItem -> {
                        String menuItemId = menuItem.getMenuItemId();
                        // Nếu món ăn vừa gọi không trùng với món ăn đã có trong hóa đơn  => INSERT
                        if(!menuItemIdList.contains(menuItemId)){
                            orderItemDAO.insert(new OrderItemEntity(orderId, menuItemId, menuItem.getQuantity(), menuItem.getCurrentPrice()));
                        }else{ // Ngược lại => UPDATE số lượng
                            orderItemDAO.updateQuantityById(tableId, menuItemId, menuItem.getQuantity());
                        }
                    });
        }
        // Cập nhập tổng tiền hóa đơn
        orderDAO.updateTotalById(orderId);
    }

    @Transactional
    @Override
    public void payOrder(PayOrderRequest request) {
        if (request.getAmountPaid().compareTo(request.getTotalAmount()) < 0) {
            throw new InternalException("Tiền khách trả phải lớn hơn hoặc bằng tổng tiền hóa đơn");
        }
        // Thanh toán hóa đơn hóa đơn
        orderDAO.payOrder(request.getOrderId(), request.getAmountPaid(), request.getChangeAmount(), OrderStatus.PAID.name());
        // Cập nhập trạng thái bàn => AVAILABLE
        tableDAO.updateStatusById(request.getSourceTableId(), TableStatus.AVAILABLE.name());
    }

    @Transactional(readOnly = true)
    @Override
    public OrderDTO getOrder(String id) {
        OrderDTO orderDTO = orderDAO.findDetailById(id)
                .orElseThrow(() -> new InternalException(ErrorMessageConstants.ORDER_NOT_FOUND + ": " + id));
        List<OrderItemDTO> orderItemList = orderItemDAO.findByOrderId(id).stream()
                .map(orderItem -> {
                    OrderItemDTO orderItemDTO = new OrderItemDTO();
                    orderItemDTO.setMenuItemId(orderItem.getMenuItemId());
                    MenuItemEntity menuItemEntity = menuItemDAO.findById(orderItem.getMenuItemId())
                            .orElseThrow(() -> new InternalException(ErrorMessageConstants.MENU_ITEM_NOT_FOUND + ": " + orderItem.getMenuItemId()));
                    orderItemDTO.setMenuItemName(menuItemEntity.getName());
                    orderItemDTO.setQuantity(orderItem.getQuantity());
                    orderItemDTO.setCurrentPrice(orderItem.getCurrentPrice());
                    BigDecimal lineTotal = orderItem.getCurrentPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
                    orderItemDTO.setLineTotal(lineTotal);
                    return orderItemDTO;
                }).toList();

        orderDTO.setOrderItemList(orderItemList);
        return orderDTO;
    }
}
