package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IEmployeeDAO;
import com.example.coffeemanagement.exception.NotFoundException;
import com.example.coffeemanagement.model.Employee;
import com.example.coffeemanagement.security.CustomUserDetail;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final IEmployeeDAO employeeDAO;

    public CustomUserDetailsService(IEmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeDAO.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.ACCOUNT_NOT_FOUND + ": " + username));
        Collection<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + employee.getRole()));
        return new CustomUserDetail(employee, authorities);
    }
}
