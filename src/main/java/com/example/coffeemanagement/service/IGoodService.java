package com.example.coffeemanagement.service;

import com.example.coffeemanagement.dto.GoodDTO;
import com.example.coffeemanagement.dto.GoodListDTO;
import com.example.coffeemanagement.dto.ImportExportGoodDTO;
import com.example.coffeemanagement.dto.PageDTO;

import java.util.List;

public interface IGoodService {
    PageDTO<GoodListDTO> getAllGoods(int page, int size, String sort, String dir, String searchValue);
    List<GoodDTO> getAllGoods();
    GoodDTO getGood(String id);
    void createGood(GoodDTO dto);
    void updateGood(String id, GoodDTO dto);
    void deleteGood(String id);
    void importOrExportGood(String id, ImportExportGoodDTO dto);

}
