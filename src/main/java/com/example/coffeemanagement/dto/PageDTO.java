package com.example.coffeemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO<T> {

    private List<T> content;
    private int currentPage;
    private int sizePage;
    private int totalPages;
    private long totalElements;
    private String sort;
    private String dir;
    private String searchValue;


}
