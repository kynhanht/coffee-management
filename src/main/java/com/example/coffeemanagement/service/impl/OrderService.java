package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.*;
import com.example.coffeemanagement.dto.OrderMenuItemDTO;
import com.example.coffeemanagement.dto.request.OrderTableRequest;
import com.example.coffeemanagement.dto.request.PayOrderRequest;
import com.example.coffeemanagement.enums.OrderStatus;
import com.example.coffeemanagement.enums.TableStatus;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.exception.NotFoundException;
import com.example.coffeemanagement.model.Order;
import com.example.coffeemanagement.model.OrderItem;
import com.example.coffeemanagement.model.ReservationDetail;
import com.example.coffeemanagement.service.IOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Override
    public String getUnpaidOrderByTableId(String tableId) {
        return orderDAO.findUnpaidOrderByTableId(tableId)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.UNPAID_ORDER_NOT_FOUND + ": " + tableId));
    }

    @Transactional
    @Override
    public void saveOrder(OrderTableRequest request) {
        String sourceTableId = request.getSourceTableId();

        // 1. Lấy hoặc tạo hóa đơn
        String orderId = orderDAO.findUnpaidOrderByTableId(sourceTableId)
                .orElseGet(() -> {
                    // Tạo chi tiết bàn mới
                    ReservationDetail reservationDetail = new ReservationDetail();
                    reservationDetail.setTableId(sourceTableId);
                    reservationDetail.setEmployeeId(request.getEmployeeId());
                    reservationDetail.setReservationDate(LocalDateTime.now());
                    reservationDetailDAO.insert(reservationDetail);
                    // Tạo hóa đơn mới
                    String id = orderDAO.generateNextId();
                    Order order = new Order();
                    order.setId(id);
                    order.setTableId(sourceTableId);
                    order.setEmployeeId(request.getEmployeeId());
                    order.setPromotionId(null);
                    order.setCustomerName(null);
                    order.setCustomerPhone(null);
                    order.setTotalAmount(BigDecimal.ZERO);
                    order.setAmountPaid(BigDecimal.ZERO);
                    order.setChangeAmount(BigDecimal.ZERO);
                    order.setCreatedDate(LocalDateTime.now());
                    order.setStatus(OrderStatus.UNPAID.name());

                    orderDAO.insert(order);
                    return id;
                });
        // 2. Xóa toàn bộ chi tiết order cũ
        orderItemDAO.deleteByOrderId(orderId);
        // 3. Insert lại danh sách order
        for (OrderMenuItemDTO item : request.getOrderList()) {
            if (item.getSelected() && item.getQuantity() > 0) {

                BigDecimal price = menuItemDAO.findById(item.getId())
                        .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.MENU_ITEM_NOT_FOUND + ": " + item.getId()))
                        .getPrice();
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(orderId);
                orderItem.setMenuItemId(item.getId());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setCurrentPrice(price);
                orderItemDAO.insert(orderItem);
            }
        }

        // 4. Update tổng tiền cho order
        orderDAO.updateTotalById(orderId);

        // 5. Update trạng thái bàn nếu cần
        tableDAO.findById(sourceTableId)
                .filter(table -> table.getStatus() != TableStatus.OCCUPIED)
                .ifPresent(table ->
                        tableDAO.updateStatus(sourceTableId, TableStatus.OCCUPIED.name()));
    }

    @Override
    public void payOrder(PayOrderRequest request) {
        if(request.getAmountPaid().compareTo(request.getTotalAmount()) < 0){
            throw new InternalException("Tiền khách trả phải lớn hơn hoặc bằng tổng tiền hóa đơn");
        }
        // Thanh toán hóa đơn hóa đơn
        orderDAO.payOrder(request.getOrderId(), request.getAmountPaid(), request.getChangeAmount());
        // Cập nhập trạng thái bàn => AVAILABLE
        tableDAO.updateStatus(request.getSourceTableId(), TableStatus.AVAILABLE.name());
    }
}
