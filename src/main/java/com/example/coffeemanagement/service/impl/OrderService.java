package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.*;
import com.example.coffeemanagement.dto.OrderDTO;
import com.example.coffeemanagement.dto.OrderItemDTO;
import com.example.coffeemanagement.dto.OrderItemSelectDTO;
import com.example.coffeemanagement.dto.request.OrderTableRequest;
import com.example.coffeemanagement.dto.request.PayOrderRequest;
import com.example.coffeemanagement.entity.MenuItemEntity;
import com.example.coffeemanagement.entity.OrderEntity;
import com.example.coffeemanagement.entity.OrderItemEntity;
import com.example.coffeemanagement.entity.ReservationDetailEntity;
import com.example.coffeemanagement.enums.OrderStatus;
import com.example.coffeemanagement.enums.TableStatus;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.exception.NotFoundException;
import com.example.coffeemanagement.service.IOrderService;
import com.example.coffeemanagement.util.SystemUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

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
    public String getTotalAmount(String id) {
        return orderDAO.findTotalAmountById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.ORDER_NOT_FOUND + ": " + id));
    }

    @Transactional
    @Override
    public void saveOrder(OrderTableRequest request) {
        String sourceTableId = request.getSourceTableId();

        // 1. Lấy hóa đơn đang sử dụng(Trạng thái: Unpaid) hoặc tạo hóa đơn mới nếu không tìm thấy
        String orderId = orderDAO.findOrderIdByTableIdAndStatus(sourceTableId, OrderStatus.UNPAID.name())
                .orElseGet(() -> {
                    // Tạo chi tiết bàn mới
                    ReservationDetailEntity reservationDetailEntity = new ReservationDetailEntity();
                    reservationDetailEntity.setTableId(sourceTableId);
                    reservationDetailEntity.setEmployeeId(request.getEmployeeId());
                    reservationDetailEntity.setReservationDate(LocalDateTime.now());
                    reservationDetailDAO.insert(reservationDetailEntity);
                    // Tạo hóa đơn mới
                    String id = orderDAO.generateNextId();
                    OrderEntity orderEntity = new OrderEntity();
                    orderEntity.setId(id);
                    orderEntity.setTableId(sourceTableId);
                    orderEntity.setEmployeeId(request.getEmployeeId());
                    orderEntity.setPromotionId(null);
                    orderEntity.setCustomerName(null);
                    orderEntity.setCustomerPhone(null);
                    orderEntity.setTotalAmount(BigDecimal.ZERO);
                    orderEntity.setAmountPaid(BigDecimal.ZERO);
                    orderEntity.setChangeAmount(BigDecimal.ZERO);
                    orderEntity.setCreatedDate(LocalDateTime.now());
                    orderEntity.setStatus(OrderStatus.UNPAID.name());

                    orderDAO.insert(orderEntity);
                    return id;
                });
        // 2. Xóa toàn bộ chi tiết order cũ
        orderItemDAO.deleteByOrderId(orderId);
        // 3. Insert lại danh sách order
        for (OrderItemSelectDTO item : request.getOrderItemList()) {
            if (item.getSelected() && item.getQuantity() > 0) {

                BigDecimal price = menuItemDAO.findById(item.getMenuItemId())
                        .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.MENU_ITEM_NOT_FOUND + ": " + item.getMenuItemId()))
                        .getPrice();
                OrderItemEntity orderItemEntity = new OrderItemEntity();
                orderItemEntity.setOrderId(orderId);
                orderItemEntity.setMenuItemId(item.getMenuItemId());
                orderItemEntity.setQuantity(item.getQuantity());
                orderItemEntity.setCurrentPrice(price);
                orderItemDAO.insert(orderItemEntity);
            }
        }

        // 4. Update tổng tiền cho order
        orderDAO.updateTotalById(orderId);

        // 5. Update trạng thái bàn nếu cần
        tableDAO.findById(sourceTableId)
                .filter(table -> !table.getStatus().equals(TableStatus.OCCUPIED.name()))
                .ifPresent(table ->
                        tableDAO.updateStatusById(sourceTableId, TableStatus.OCCUPIED.name()));
    }

    @Transactional
    @Override
    public void payOrder(PayOrderRequest request) {
        BigDecimal totalAmount = SystemUtils.StringToBigDecimal(request.getTotalAmount());
        BigDecimal amountPaid = SystemUtils.StringToBigDecimal(request.getAmountPaid());
        BigDecimal changeAmount = SystemUtils.StringToBigDecimal(request.getChangeAmount());
        if(amountPaid.compareTo(totalAmount) < 0){
            throw new InternalException("Tiền khách trả phải lớn hơn hoặc bằng tổng tiền hóa đơn");
        }
        // Thanh toán hóa đơn hóa đơn
        orderDAO.payOrder(request.getOrderId(), amountPaid, changeAmount, OrderStatus.PAID.name());
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
                    orderItemDTO.setCurrentPrice(SystemUtils.bigDecimalToString(orderItem.getCurrentPrice(), Locale.US));
                    BigDecimal lineTotal = orderItem.getCurrentPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
                    orderItemDTO.setLineTotal(SystemUtils.bigDecimalToString(lineTotal, Locale.US));
                    return orderItemDTO;
                }).toList();

        orderDTO.setOrderItemList(orderItemList);
        return orderDTO;
    }
}
