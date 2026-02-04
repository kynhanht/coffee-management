package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dto.CustomUserDetail;
import com.example.coffeemanagement.entity.TaiKhoan;
import com.example.coffeemanagement.repository.TaiKhoanRepository;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final TaiKhoanRepository taiKhoanRepository;

    public CustomUserDetailsService(TaiKhoanRepository taiKhoanRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TaiKhoan taiKhoan = taiKhoanRepository
                .findByTenDangNhap(username)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessageConstants.TAI_KHOAN_NOT_FOUND));

        Collection<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_"+taiKhoan.getQuyenHan()));
        CustomUserDetail customUserDetail = new CustomUserDetail(taiKhoan, authorities);
        return customUserDetail;
    }
}
