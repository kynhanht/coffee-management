package com.example.coffeemanagement.converter;

import com.example.coffeemanagement.dto.TaiKhoanDTO;
import com.example.coffeemanagement.model.TaiKhoanModel;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TaiKhoanConverter {

    private final ModelMapper modelMapper;

    public TaiKhoanConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public TaiKhoanDTO converterToDTO(TaiKhoanModel model){
        TaiKhoanDTO dto;
        dto = modelMapper.map(model,TaiKhoanDTO.class);
        return dto;
    }

    public TaiKhoanModel convertToModel(TaiKhoanDTO dto){
        TaiKhoanModel model;
        model = modelMapper.map(dto, TaiKhoanModel.class);
        return model;
    }
}
