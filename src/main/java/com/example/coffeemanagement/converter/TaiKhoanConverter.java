package com.example.coffeemanagement.converter;

import com.example.coffeemanagement.dto.TaiKhoanDTO;
import com.example.coffeemanagement.entity.TaiKhoan;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TaiKhoanConverter {

    private final ModelMapper modelMapper;

    public TaiKhoanConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public TaiKhoanDTO converterToDTO(TaiKhoan entity){
        TaiKhoanDTO dto;
        dto = modelMapper.map(entity,TaiKhoanDTO.class);
        return dto;
    }

    public TaiKhoan convertToEntity(TaiKhoanDTO dto){
        TaiKhoan entity;
        entity = modelMapper.map(dto, TaiKhoan.class);
        return entity;
    }
}
