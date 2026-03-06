package com.example.coffeemanagement.dto;

import java.util.List;

public class PageDTO<T> {

    private List<T> content;
    private int currentPage;
    private int sizePage;
    private int totalPages;
    private long totalElements;
    private String sort;
    private String dir;
    private String searchValue;


    public PageDTO(List<T> content, int currentPage, int sizePage, int totalPages, long totalElements, String sort, String dir, String searchValue) {
        this.content = content;
        this.currentPage = currentPage;
        this.sizePage = sizePage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.sort = sort;
        this.dir = dir;
        this.searchValue = searchValue;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getSizePage() {
        return sizePage;
    }

    public void setSizePage(int sizePage) {
        this.sizePage = sizePage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }
}
