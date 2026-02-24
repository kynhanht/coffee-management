package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.ITaiKhoanDAO;
import com.example.coffeemanagement.exception.NotFoundException;
import com.example.coffeemanagement.model.TaiKhoan;
import com.example.coffeemanagement.security.CustomUserDetail;
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

    private final ITaiKhoanDAO ITaiKhoanDao;

    public CustomUserDetailsService(ITaiKhoanDAO ITaiKhoanDao) {
        this.ITaiKhoanDao = ITaiKhoanDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TaiKhoan taiKhoan = ITaiKhoanDao.findByTenDangNhap(username)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.TAI_KHOAN_NOT_FOUND + ": " + username));
        Collection<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + taiKhoan.getQuyenHan()));
        CustomUserDetail customUserDetail = new CustomUserDetail(taiKhoan, authorities);
        return customUserDetail;
    }
}
