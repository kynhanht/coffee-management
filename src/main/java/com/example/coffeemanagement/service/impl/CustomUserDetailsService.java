package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.dao.TaiKhoanDAO;
import com.example.coffeemanagement.dto.CustomUserDetail;
import com.example.coffeemanagement.model.TaiKhoanModel;
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

    private final TaiKhoanDAO taiKhoanDao;

    public CustomUserDetailsService(TaiKhoanDAO taiKhoanDao) {
        this.taiKhoanDao = taiKhoanDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TaiKhoanModel model = taiKhoanDao
                .findByTenDangNhap(username);

        Collection<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_"+model.getQuyenHan()));
        CustomUserDetail customUserDetail = new CustomUserDetail(model, authorities);
        return customUserDetail;
    }
}
