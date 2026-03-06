package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.constant.SystemConstants;
import com.example.coffeemanagement.dao.INhanVienDAO;
import com.example.coffeemanagement.dto.NhanVienDTO;
import com.example.coffeemanagement.dto.NhanVienDetailDTO;
import com.example.coffeemanagement.dto.NhanVienListDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.exception.NotFoundException;
import com.example.coffeemanagement.model.NhanVien;
import com.example.coffeemanagement.service.IFileStorageService;
import com.example.coffeemanagement.service.INhanVienService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NhanVienService implements INhanVienService {

    private final INhanVienDAO nhanVienDAO;
    private final IFileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;

    public NhanVienService(INhanVienDAO nhanVienDAO, IFileStorageService fileStorageService, PasswordEncoder passwordEncoder) {
        this.nhanVienDAO = nhanVienDAO;
        this.fileStorageService = fileStorageService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public NhanVienDetailDTO getDetail(String tenDangNhap) {
        return nhanVienDAO.findDetailByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.NHAN_VIEN_NOT_FOUND + ": " + tenDangNhap));
    }

    public void updateProfile(String tenDangNhap, NhanVienDetailDTO dto) {
        NhanVien nhanVien = new NhanVien();
        // Quyền hạn
        String quyenHan = dto.getMaChucVu().equals(SystemConstants.CODE_ROLE_ADMIN) ? SystemConstants.ROLE_ADMIN : SystemConstants.ROLE_USER;
        // Lưu file
        String fileName = fileStorageService.storeFile(dto.getFile());

        if(fileName != null) nhanVien.setAnh(fileName);

        nhanVien.setMaNhanVien(dto.getMaNhanVien());
        nhanVien.setMaChucVu(dto.getMaChucVu());
        nhanVien.setHoTen(dto.getHoTen());
        nhanVien.setSoDienThoai(dto.getSoDienThoai());
        nhanVien.setDiaChi(dto.getDiaChi());
        nhanVien.setTenDangNhap(tenDangNhap);
        nhanVien.setMatKhau(dto.getMatKhau());
        nhanVien.setQuyenHan(quyenHan);

        int rows = nhanVienDAO.updateProfile(tenDangNhap, nhanVien);
        if (rows == 0) {
            throw new NotFoundException(ErrorMessageConstants.TAI_KHOAN_NOT_FOUND + " : " + dto.getTenDangNhap());
        }
    }

    @Override
    public PageDTO<NhanVienListDTO> getAll(int page, int size, String sort, String dir, String searchValue) {
        return nhanVienDAO.findAll(page, size, sort, dir, searchValue);
    }
    @Transactional
    @Override
    public void createNhanVien(NhanVienDTO dto) {
        // Sinh mã
        String maNhanVien = nhanVienDAO.generateNextId();

        // Lưu file
        String fileName = fileStorageService.storeFile(dto.getFile());
        // mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(dto.getMatKhau());

        // Quyền hạn
        String quyenHan = dto.getMaChucVu().equals(SystemConstants.CODE_ROLE_ADMIN) ? SystemConstants.ROLE_ADMIN : SystemConstants.ROLE_USER;

        // Trạng thái
        String trangThai = SystemConstants.ACTIVE_USER;

        // Insert nhân viên
        NhanVien nhanVien = new NhanVien(
                maNhanVien,
                dto.getMaChucVu(),
                dto.getHoTen(),
                dto.getSoDienThoai(),
                dto.getDiaChi(),
                fileName,
                dto.getTenDangNhap(),
                encodedPassword,
                quyenHan,
                trangThai
                );
        nhanVienDAO.insert(nhanVien);

    }

    @Override
    public NhanVienDTO getNhanVienById(String maNhanVien) {
        return nhanVienDAO.findById(maNhanVien)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.NHAN_VIEN_NOT_FOUND + ": " + maNhanVien));
    }

    @Transactional
    @Override
    public void updateNhanVien(String maNhanVien, NhanVienDTO dto) {
        // Lưu file
        String fileName = fileStorageService.storeFile(dto.getFile());

        if(fileName != null){
            dto.setAnh(fileName);
        }
        // Quyền hạn
        String quyenHan = dto.getMaChucVu().equals(SystemConstants.CODE_ROLE_ADMIN) ? SystemConstants.ROLE_ADMIN : SystemConstants.ROLE_USER;
        // mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(dto.getMatKhau());

        // Insert nhân viên
        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNhanVien(maNhanVien);
        nhanVien.setMaChucVu(dto.getMaChucVu());
        nhanVien.setHoTen(dto.getHoTen());
        nhanVien.setSoDienThoai(dto.getSoDienThoai());
        nhanVien.setDiaChi(dto.getDiaChi());
        nhanVien.setAnh(dto.getAnh());
        nhanVien.setTenDangNhap(dto.getTenDangNhap());
        nhanVien.setMatKhau(encodedPassword);
        nhanVien.setQuyenHan(quyenHan);
        nhanVienDAO.update(maNhanVien, nhanVien);

    }
    @Transactional
    @Override
    public void deleteNhanVien(String maNhanVien) {
        nhanVienDAO.findById(maNhanVien)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.NHAN_VIEN_NOT_FOUND + ": " + maNhanVien));
        nhanVienDAO.delete(maNhanVien);
    }


}
