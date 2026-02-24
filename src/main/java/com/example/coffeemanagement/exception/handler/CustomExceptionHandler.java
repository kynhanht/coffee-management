package com.example.coffeemanagement.exception.handler;

import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CustomExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(InternalException.class)
    public String handleInternal(InternalException ex, Model model) {
        logger.error("Lỗi hệ thống", ex);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/501";
    }

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException ex, Model model) {
        logger.error("Lỗi hệ thống", ex);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    @ModelAttribute("currentUser")
    public String currentUser() {
        return "Nguyễn Văn A";
    }
}
