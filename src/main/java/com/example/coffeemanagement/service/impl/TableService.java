package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.*;
import com.example.coffeemanagement.dto.*;
import com.example.coffeemanagement.dto.request.*;
import com.example.coffeemanagement.enums.OrderStatus;
import com.example.coffeemanagement.enums.TableStatus;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.exception.NotFoundException;
import com.example.coffeemanagement.model.Order;
import com.example.coffeemanagement.model.OrderItem;
import com.example.coffeemanagement.model.ReservationDetail;
import com.example.coffeemanagement.service.ITableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableService implements ITableService {

    private final IReservationDetailDAO reservationDetailDAO;
    private final ITableDAO tableDAO;
    private final IOrderDAO orderDAO;
    private final IOrderItemDAO orderItemDAO;
    private final IMenuItemDAO menuItemDAO;

    public TableService(IReservationDetailDAO reservationDetailDAO, ITableDAO tableDAO, IOrderDAO orderDAO, IOrderItemDAO orderItemDAO, IMenuItemDAO menuItemDAO) {
        this.reservationDetailDAO = reservationDetailDAO;
        this.tableDAO = tableDAO;
        this.orderDAO = orderDAO;
        this.orderItemDAO = orderItemDAO;
        this.menuItemDAO = menuItemDAO;
    }

    @Transactional(readOnly = true)
    @Override
    public TableDTO getById(String id) {
        return tableDAO.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.TABLE_NOT_FOUND + ": " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<TableDTO> getAll() {
        return tableDAO.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public TableInfoDTO getTableInfo(String id) {
        return tableDAO.findTableInfo(id).orElseThrow(
                () -> new NotFoundException(ErrorMessageConstants.TABLE_NOT_FOUND + ": " + id));
    }

    @Transactional
    @Override
    public void reserveTable(ReserveTableRequest request) {
        tableDAO.findById(request.getSourceTableId())
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.TABLE_NOT_FOUND + ": " + request.getSourceTableId()));

        // Tạo chi tiết đặt bàn mới
        ReservationDetail model = new ReservationDetail(
                request.getSourceTableId(),
                request.getEmployeeId(),
                request.getCustomerName(),
                request.getCustomerPhone(),
                request.getReservationDate());
        reservationDetailDAO.insert(model);

        // Update bàn
        tableDAO.updateStatus(request.getSourceTableId(), TableStatus.RESERVED.name());

    }

    @Transactional
    @Override
    public void moveTable(MoveTableRequest request) {
        if (request.getSourceTableId().equals(request.getTargetTableId())) {
            throw new InternalException("Không thể chuyển cùng 1 bàn");
        }
        // 1. Cập nhập chi tiết bàn cũ sang chi tiết bàn mới
        reservationDetailDAO.updateTableId(request.getSourceTableId(), request.getTargetTableId());

        // 2. Copy trạng thái bàn cũ sang bàn mới
        tableDAO.copyStatus(request.getSourceTableId(), request.getTargetTableId());

        // 3. Lấy mã hóa đơn bàn nguồn
        String orderId = orderDAO.findUnpaidOrderByTableId(request.getSourceTableId())
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.UNPAID_ORDER_NOT_FOUND + ": " + request.getSourceTableId()));

        // 4. Cập nhập Hóa đơn bàn nguồn sang bàn đích
        orderDAO.updateTableIdById(orderId, request.getTargetTableId());

        // 5. Cập nhập trạng thái bàn cũ -> trống
        tableDAO.updateStatus(request.getSourceTableId(), TableStatus.AVAILABLE.name());

    }
    @Transactional(readOnly = true)
    @Override
    public List<TableOptionDTO> getSelectableTables(String id, List<TableStatus> statues) {
        return tableDAO.findByStatuses(statues)
                .stream()
                .filter(table -> !table.getId().equals(id))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void mergeTables(MergeTableRequest request) {
        // Danh sách các bàn nguồn
        List<String> sourceTableIds = request
                .getMergeableSourceTableList().stream()
                .filter(TableOptionDTO::getSelected)
                .map(TableOptionDTO::getId)
                .collect(Collectors.toList());
        sourceTableIds.add(0, request.getSourceTableId());
        // Bàn đích
        String targetTableId = request.getTargetTableId();
        // Kiểm tra xem các bàn nguồn có bàn nào trùng với bàn đích không, nếu trùng thì xóa ra khỏi danh sách bàn nguồn
        if (sourceTableIds.contains(targetTableId)) {
            sourceTableIds.remove(targetTableId);
        }

        // Lấy thông tin bàn đích
        TableDTO targetTable = tableDAO.findById(targetTableId)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.TABLE_NOT_FOUND + ": " + targetTableId));

        // Lấy mã hóa đơn của các bàn nguồn
        List<String> sourceOrderIds =
                sourceTableIds.stream()
                        .map(tableId -> orderDAO.findUnpaidOrderByTableId(tableId)
                                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.TABLE_NOT_FOUND + ": " + tableId)))
                        .toList();
        String targetOrderId;
        List<MergedItemDTO> mergedItems;
        ReservationDetail reservationDetail = new ReservationDetail(
                targetTableId,
                request.getEmployeeId(),
                request.getCustomerName(),
                request.getCustomerPhone(),
                LocalDateTime.now()
        );

        // 1. Xóa chi tiết đặt bàn của các bàn nguồn
        sourceTableIds
                .forEach(reservationDetailDAO::deleteByTableId);

        // TH1: BÀN ĐÍCH = AVAILABLE
        if (targetTable.getStatus() == TableStatus.AVAILABLE) {
            // Tạo chi tiết đặt bàn đích
            reservationDetailDAO.insert(reservationDetail);
            // Tạo hóa đơn cho bàn đích
            if (sourceTableIds.size() > 1) {
                targetOrderId = String.join("_MERGE_", sourceOrderIds);
            } else {
                targetOrderId = orderDAO.generateNextId();
            }
            Order order = new Order(
                    targetOrderId,
                    targetTableId,
                    request.getEmployeeId(),
                    null,
                    request.getCustomerName(),
                    request.getCustomerPhone(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    LocalDateTime.now(),
                    OrderStatus.UNPAID.name()
            );
            orderDAO.insert(order);
            // Lấy danh sách chi tiết hóa đơn của bàn nguồn
            mergedItems = orderItemDAO.findMergedItems(sourceOrderIds);
        } else { // TH2: BÀN ĐÍCH = OCCUPIED
            // Cập nhập chi tiết đặt bàn đích
            reservationDetailDAO.updateByTableId(targetTableId, reservationDetail);
            // Lấy danh sách chi tiết hóa đơn mới
            targetOrderId = orderDAO.findUnpaidOrderByTableId(targetTableId)
                    .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.TABLE_NOT_FOUND + ": " + targetTableId));
            List<String> newOrderIds = new ArrayList<>(sourceOrderIds);
            newOrderIds.add(0, targetOrderId);
            mergedItems = orderItemDAO.findMergedItems(newOrderIds);
            // Xóa chi tiết hóa đơn của bàn đích
            orderItemDAO.deleteByOrderId(targetOrderId);
        }

        // 2. Cập nhập lại trạng thái hóa đơn của các bàn nguồn
        sourceOrderIds.forEach(orderId -> orderDAO.updateStatusById(orderId, OrderStatus.CANCELLED.name()));

        // 3. INSERT lại chi tiết hóa đơn cho bàn đích
        for (MergedItemDTO item : mergedItems) {
            OrderItem orderItem = new OrderItem(targetOrderId, item.getId(), item.getQuantity(), item.getCurrentPrice());
            orderItemDAO.insert(orderItem);
        }

        // 4. Update tổng tiền
        orderDAO.updateTotalById(targetOrderId);

        // 5. Update trạng thái các bàn nguồn
        sourceTableIds.forEach(tableId -> tableDAO.updateStatus(tableId, TableStatus.AVAILABLE.name()));

        // 6. Update bàn đích => OCCUPIED (nếu đang AVAILABLE)
        if (targetTable.getStatus() == TableStatus.AVAILABLE) {
            tableDAO.updateStatus(targetTableId, TableStatus.OCCUPIED.name());
        }


    }
    @Transactional
    @Override
    public void splitTable(SplitTableRequest request) {
        if (request.getSourceTableId().equals(request.getTargetTableId())) {
            throw new InternalException("Không thể tách chung 1 bàn");
        }
        String sourceTableId = request.getSourceTableId();
        String targetTableId = request.getTargetTableId();
        // Lọc ra các món ăn cần tách
        List<OrderMenuItemDTO> splitOrderList = request
                .getSplitOrderList().stream()
                .filter(OrderMenuItemDTO::getSelected)
                .toList();
        if(splitOrderList.isEmpty()){
            throw new InternalException("Không có món ăn nào được tách");
        }

        String sourceOrderId = orderDAO.findUnpaidOrderByTableId(sourceTableId).orElseThrow(() -> new NotFoundException(ErrorMessageConstants.TABLE_NOT_FOUND + ": " + sourceTableId));
        String targetOrderId;
        TableDTO targetTable = tableDAO.findById(targetTableId)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.TABLE_NOT_FOUND + ": " + targetTableId));

        // TH1: Bàn đích -> AVAILABLE
        if (targetTable.getStatus() == TableStatus.AVAILABLE) {

            // 1. Tạo mới chi tiết đặt bàn đích
            ReservationDetail reservationDetail = new ReservationDetail(
                    targetTableId,
                    request.getEmployeeId(),
                    request.getCustomerName(),
                    request.getCustomerPhone(),
                    LocalDateTime.now()
            );
            reservationDetailDAO.insert(reservationDetail);

            // 2. Tạo mới hóa đơn bàn đích
            targetOrderId = orderDAO.generateNextId();
            Order order = new Order(
                    targetOrderId,
                    targetTableId,
                    request.getEmployeeId(),
                    null,
                    request.getCustomerName(),
                    request.getCustomerPhone(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    LocalDateTime.now(),
                    OrderStatus.UNPAID.name()
            );
            orderDAO.insert(order);
            // 3. Tạo mới chi tiết hóa đơn bàn đích
            splitOrderList
                    .forEach(menuItem -> {
                        MenuItemDTO menuItemDTO = menuItemDAO.findById(menuItem.getId())
                                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.MENU_ITEM_NOT_FOUND + ": " + menuItem.getId()));
                        OrderItem orderItem = new OrderItem(targetOrderId, menuItem.getId(), menuItem.getQuantity(), menuItemDTO.getPrice());
                        orderItemDAO.insert(orderItem);
                    });
            // 4. Cập nhập số lượng của chi tiết hóa đơn bàn nguồn
            splitOrderList
                    .forEach(menuItem -> orderItemDAO.updateQuantityById(sourceOrderId, menuItem.getId(), - menuItem.getQuantity()));
            // 5. Cập nhập tổng tiền hóa đơn của bàn nguồn
            orderDAO.updateTotalById(sourceOrderId);

            // 6. Cập nhập tổng tiền hóa đơn của bàn đích
            orderDAO.updateTotalById(targetOrderId);

            // 7. Cập nhập trạng thái bàn đích => OCCUPIED
            tableDAO.updateStatus(targetTableId, TableStatus.OCCUPIED.name());
        }else if(targetTable.getStatus() == TableStatus.OCCUPIED){
            // Lấy các món ăn đã gọi của bàn đích
            List<String> targetMenuItemIdList = orderItemDAO.findOrderByTableId(targetTableId).stream()
                    .map(OrderMenuItemDTO::getId).toList();
            // Lấy mã order của bàn đích
            targetOrderId = orderDAO.findUnpaidOrderByTableId(targetTableId).orElseThrow(() -> new NotFoundException(ErrorMessageConstants.TABLE_NOT_FOUND + ": " + targetTableId));
            // 1. Chỉnh sửa chi tiết hóa đơn của bàn đích(nếu món ăn tách ra không trùng với bàn nguồn => INSERT, Ngược lại => UPDATE số lượng)
            splitOrderList
                    .forEach(menuItem -> {
                        String menuItemId = menuItem.getId();
                        // nếu món ăn ở bàn đích không trùng với bàn nguồn => INSERT
                        if(!targetMenuItemIdList.contains(menuItemId)){
                            MenuItemDTO menuItemDTO = menuItemDAO.findById(menuItem.getId())
                                    .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.MENU_ITEM_NOT_FOUND + ": " + menuItem.getId()));
                            OrderItem orderItem = new OrderItem(targetOrderId, menuItemId, menuItem.getQuantity(), menuItemDTO.getPrice());
                            orderItemDAO.insert(orderItem);
                        }else{ // Ngược lại => UPDATE số lượng
                            orderItemDAO.updateQuantityById(targetOrderId, menuItemId, menuItem.getQuantity());
                            // 2. Cập nhập số lượng của chi tiết hóa đơn bàn nguồn
                            orderItemDAO.updateQuantityById(sourceOrderId, menuItemId, - menuItem.getQuantity());
                        }
                    });
            // 3. Cập nhập tổng tiền hóa đơn của bàn nguồn
            orderDAO.updateTotalById(sourceOrderId);

            // 4. Cập nhập tổng tiền hóa đơn của bàn đích
            orderDAO.updateTotalById(targetOrderId);
        }else{
            throw new InternalException("Không thể tách bàn!");
        }
    }

    @Transactional
    @Override
    public void cancelTable(String id) {
        tableDAO.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.TABLE_NOT_FOUND + ": " + id));
        // 1. Xóa Chi tiết đặt bàn 01
        reservationDetailDAO.deleteByTableId(id);
        // 2. Cập nhập trạng thái hóa đơn => CANCELLED
        orderDAO.updateStatusById(id, OrderStatus.CANCELLED.name());
        //3. Cập nhập trạng thái bàn => AVAILABLE
        tableDAO.updateStatus(id, TableStatus.AVAILABLE.name());
    }

}
