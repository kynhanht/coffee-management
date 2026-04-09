package com.example.coffeemanagement.converter;

import com.example.coffeemanagement.dto.EmployeeDetailDTO;
import com.example.coffeemanagement.model.Account;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AccountConverter {

    private final ModelMapper modelMapper;

    public AccountConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public EmployeeDetailDTO converterToDTO(Account model){
        EmployeeDetailDTO dto;
        dto = modelMapper.map(model, EmployeeDetailDTO.class);
        return dto;
    }

    public Account convertToModel(EmployeeDetailDTO dto){
        Account model;
        model = modelMapper.map(dto, Account.class);
        return model;
    }
}
