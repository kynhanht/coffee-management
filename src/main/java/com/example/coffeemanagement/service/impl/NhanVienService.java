package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.INhanVienDAO;
import com.example.coffeemanagement.dto.NhanVienDetailDTO;
import com.example.coffeemanagement.exception.NotFoundException;
import com.example.coffeemanagement.service.INhanVienService;
import org.springframework.stereotype.Service;

@Service
public class NhanVienService implements INhanVienService {

    private final INhanVienDAO nhanVienDAO;

    public NhanVienService(INhanVienDAO nhanVienDAO) {
        this.nhanVienDAO = nhanVienDAO;
    }

    @Override
    public NhanVienDetailDTO getDetail(String tenDangNhap) {
        return nhanVienDAO.findDetailByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.NHAN_VIEN_NOT_FOUND + ": " + tenDangNhap));
    }

    public void updateNhanVien(NhanVienDetailDTO dto) {

        int rows = nhanVienDAO.updateByTenDangNhap(dto);
        if (rows == 0) {
            throw new NotFoundException(ErrorMessageConstants.TAI_KHOAN_NOT_FOUND + " : " + dto.getTenDangNhap());
        }
    }
}
