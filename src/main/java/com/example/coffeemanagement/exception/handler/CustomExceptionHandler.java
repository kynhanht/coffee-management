package com.example.coffeemanagement.exception.handler;

import com.example.coffeemanagement.exception.FileStorageException;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(InternalException.class)
    public String handleInternal(InternalException ex, Model model) {
        logger.error("Internal error", ex);
        model.addAttribute(
                "error",
                "System error occurred"
        );
        return "error/500";
    }

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException ex, Model model) {
        logger.error("Not found error", ex);
        model.addAttribute(
                "error",
                "Not found occurred"
        );
        return "error/404";
    }

    @ExceptionHandler(FileStorageException.class)
    public String handleFile(NotFoundException ex, Model model) {
        logger.error("File error", ex);
        model.addAttribute(
                "error",
                "File occurred"
        );
        return "error/500";
    }
}
