package com.example.coffeemanagement.dto;

import com.example.coffeemanagement.model.TaiKhoanModel;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetail implements UserDetails {
    private final TaiKhoanModel taiKhoanModel;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetail(TaiKhoanModel taiKhoanModel, Collection<? extends GrantedAuthority> authorities) {
        this.taiKhoanModel = taiKhoanModel;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return taiKhoanModel.getMatKhau();
    }

    @Override
    public String getUsername() {
        return taiKhoanModel.getTenDangNhap();
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
