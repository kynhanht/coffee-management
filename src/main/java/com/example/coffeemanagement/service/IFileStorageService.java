package com.example.coffeemanagement.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFileStorageService {

    String storeFile(MultipartFile file);

    void deleteFile(String filePath);
}
