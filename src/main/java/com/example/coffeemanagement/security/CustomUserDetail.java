package com.example.coffeemanagement.security;

import com.example.coffeemanagement.model.NhanVien;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetail implements UserDetails {
    private final NhanVien nhanVien;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetail(NhanVien nhanVien, Collection<? extends GrantedAuthority> authorities) {
        this.nhanVien = nhanVien;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return nhanVien.getMatKhau();
    }

    @Override
    public String getUsername() {
        return nhanVien.getTenDangNhap();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
