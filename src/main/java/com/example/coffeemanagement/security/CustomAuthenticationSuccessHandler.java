package com.example.coffeemanagement.security;

import com.example.coffeemanagement.util.SecurityUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            if (response.isCommitted()) {
                logger.error("Can't redirect");
            }
            logger.info("Login username: {}, Role: {}", SecurityUtils.getPrincipal().getUsername(), SecurityUtils.getAuthorities());
            response.sendRedirect("/");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


}
