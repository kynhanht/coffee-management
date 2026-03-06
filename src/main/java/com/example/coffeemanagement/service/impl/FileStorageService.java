package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.config.FileStorageProperties;
import com.example.coffeemanagement.exception.FileStorageException;
import com.example.coffeemanagement.service.IFileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService implements IFileStorageService {

    private final Path uploadPath;
    public FileStorageService(FileStorageProperties properties) {

        this.uploadPath = Paths.get(properties.getUploadDir())
                .toAbsolutePath()
                .normalize();
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) return null;

            if (!file.getContentType().startsWith("image/")) {
                throw new FileStorageException("Chỉ được upload file ảnh");
            }

            String ext = "";
            String original = file.getOriginalFilename();
            int dot = original.lastIndexOf(".");
            if (dot > 0) ext = original.substring(dot);

            String fileName = UUID.randomUUID() + ext;

            Path target = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), target,
                    StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException  e){
            throw new FileStorageException("Upload file thất bại", e);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isBlank()) return;
        try {
            Files.deleteIfExists(uploadPath.resolve(fileName));
        } catch (IOException e) {
            throw new FileStorageException("Xóa file thất bại", e);
        }
    }
}
