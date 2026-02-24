package com.example.coffeemanagement.converter;

import com.example.coffeemanagement.dto.NhanVienDetailDTO;
import com.example.coffeemanagement.model.TaiKhoan;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TaiKhoanConverter {

    private final ModelMapper modelMapper;

    public TaiKhoanConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public NhanVienDetailDTO converterToDTO(TaiKhoan model){
        NhanVienDetailDTO dto;
        dto = modelMapper.map(model, NhanVienDetailDTO.class);
        return dto;
    }

    public TaiKhoan convertToModel(NhanVienDetailDTO dto){
        TaiKhoan model;
        model = modelMapper.map(dto, TaiKhoan.class);
        return model;
    }
}
