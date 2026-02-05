package com.jpriva.erpsp.shared.springboot.security;

import com.jpriva.erpsp.shared.domain.model.TenantId;
import com.jpriva.erpsp.shared.domain.model.UserId;
import jakarta.annotation.Nonnull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public record AuthenticatedUser(
        UserId userId,
        String email,
        TenantId tenantId,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    @Nonnull
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Nonnull
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}