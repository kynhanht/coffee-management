package com.example.coffeemanagement.service.impl;


import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IExportGoodDetailDAO;
import com.example.coffeemanagement.dao.IGoodDAO;
import com.example.coffeemanagement.dao.IImportGoodDetailDAO;
import com.example.coffeemanagement.dto.GoodDTO;
import com.example.coffeemanagement.dto.GoodListDTO;
import com.example.coffeemanagement.dto.ImportExportGoodDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.entity.ExportGoodDetailEntity;
import com.example.coffeemanagement.entity.GoodEntity;
import com.example.coffeemanagement.entity.ImportGoodDetailEntity;
import com.example.coffeemanagement.enums.RecordStatus;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.exception.NotFoundException;
import com.example.coffeemanagement.service.IGoodService;
import com.example.coffeemanagement.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodService implements IGoodService {

    private final IGoodDAO goodDAO;
    private final IImportGoodDetailDAO importGoodDetailDAO;
    private final IExportGoodDetailDAO exportGoodDetailDAO;

    public GoodService(IGoodDAO goodDAO, IImportGoodDetailDAO importGoodDetailDAO, IExportGoodDetailDAO exportGoodDetailDAO) {
        this.goodDAO = goodDAO;
        this.importGoodDetailDAO = importGoodDetailDAO;
        this.exportGoodDetailDAO = exportGoodDetailDAO;
    }

    @Transactional(readOnly = true)
    @Override
    public PageDTO<GoodListDTO> getAllGoods(int page, int size, String sort, String dir, String searchValue) {
        return goodDAO.findAll(page, size, sort, dir, searchValue);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GoodDTO> getAllGoods() {
        List<GoodEntity> list = goodDAO.findAll();
        return list.stream().map(entity -> {
            GoodDTO dto = new GoodDTO();
            dto.setId(entity.getId());
            dto.setName(entity.getName());
            dto.setQuantity(entity.getQuantity());
            dto.setPrice(entity.getPrice());
            dto.setUnitId(entity.getUnitId());
            dto.setImportDate(entity.getImportDate());
            dto.setExportDate(entity.getExportDate());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public GoodDTO getGood(String id) {
        // Check whether the good ID exists
        GoodEntity entity = goodDAO.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.DEVICE_NOT_FOUND + ": " + id));
        // Get good
        GoodDTO dto = new GoodDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setQuantity(entity.getQuantity());
        dto.setPrice(entity.getPrice());
        dto.setUnitId(entity.getUnitId());
        dto.setImportDate(entity.getImportDate());
        dto.setExportDate(entity.getExportDate());
        return dto;
    }

    @Transactional
    @Override
    public void createGood(GoodDTO dto) {
        // Generate ID
        String id = goodDAO.generateNextId();
        // Insert Good
        GoodEntity good = new GoodEntity(
                id,
                dto.getName(),
                dto.getQuantity(),
                dto.getUnitId(),
                dto.getPrice(),
                dto.getImportDate(),
                null,
                RecordStatus.ACTIVE.name()
        );
        int rows = goodDAO.insert(good);
        if (rows == 0) {
            throw new NotFoundException(ErrorMessageConstants.GOOD_NOT_FOUND + " : " + id);
        }
        // Insert Import Good
        ImportGoodDetailEntity importGoodDetailEntity = new ImportGoodDetailEntity();
        String importGoodDetailId = importGoodDetailDAO.generateNextId();
        importGoodDetailEntity.setId(importGoodDetailId);
        importGoodDetailEntity.setEmployeeId(SecurityUtils.getPrincipal().getEmployeeEntity().getId());
        importGoodDetailEntity.setGoodId(dto.getId());
        importGoodDetailEntity.setImportDate(dto.getImportDate());
        importGoodDetailEntity.setPrice(dto.getPrice());
        importGoodDetailEntity.setQuantity(dto.getQuantity());
        importGoodDetailDAO.insert(importGoodDetailEntity);
    }

    @Transactional
    @Override
    public void updateGood(String id, GoodDTO dto) {
        // Update Good
        GoodEntity good = new GoodEntity();
        good.setId(id);
        good.setName(dto.getName());
        good.setQuantity(dto.getQuantity());
        good.setPrice(dto.getPrice());
        good.setUnitId(dto.getUnitId());
        good.setImportDate(dto.getImportDate());
        good.setExportDate(dto.getExportDate());
        goodDAO.updateById(id, good);
    }

    @Transactional
    @Override
    public void deleteGood(String id) {
        // Check whether the good ID exists
        goodDAO.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.GOOD_NOT_FOUND + ": " + id));
        // Update good status from ACTIVE to INACTIVE
        goodDAO.updateStatusById(id, RecordStatus.INACTIVE.name());
    }

    @Transactional
    @Override
    public void importOrExportGood(String id, ImportExportGoodDTO dto) {

        // Check whether the good ID exists
        GoodEntity entity = goodDAO.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.DEVICE_NOT_FOUND + ": " + id));

        // Check whether the importDate exists => import
        if(dto.getImportDate() != null){
            ImportGoodDetailEntity importGoodDetailEntity = new ImportGoodDetailEntity();
            String importGoodDetailId = importGoodDetailDAO.generateNextId();
            importGoodDetailEntity.setId(importGoodDetailId);
            importGoodDetailEntity.setEmployeeId(SecurityUtils.getPrincipal().getEmployeeEntity().getId());
            importGoodDetailEntity.setGoodId(dto.getGoodId());
            importGoodDetailEntity.setImportDate(dto.getImportDate());
            importGoodDetailEntity.setPrice(entity.getPrice());
            importGoodDetailEntity.setQuantity(dto.getQuantity());
            goodDAO.updateImport(id, dto);
            importGoodDetailDAO.insert(importGoodDetailEntity);
        } else if(dto.getExportDate() != null){ // Check whether the exportDate exists => export
            if(dto.getQuantity() > entity.getQuantity()){
                throw new InternalException("Số lượng xuất vượt quá giới hạn");
            }
            ExportGoodDetailEntity exportGoodDetailEntity = new ExportGoodDetailEntity();
            String exportGoodDetailId= exportGoodDetailDAO.generateNextId();
            exportGoodDetailEntity.setId(exportGoodDetailId);
            exportGoodDetailEntity.setEmployeeId(SecurityUtils.getPrincipal().getEmployeeEntity().getId());
            exportGoodDetailEntity.setGoodId(dto.getGoodId());
            exportGoodDetailEntity.setExportDate(dto.getExportDate());
            exportGoodDetailEntity.setPrice(entity.getPrice());
            exportGoodDetailEntity.setQuantity(dto.getQuantity());
            goodDAO.updateExport(id, dto);
            exportGoodDetailDAO.insert(exportGoodDetailEntity);
        }else{
            throw new InternalException("Chưa chọn ngày nhập hoặc ngày xuất");
        }
    }
}
