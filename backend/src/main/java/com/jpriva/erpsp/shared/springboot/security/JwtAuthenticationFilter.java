package com.jpriva.erpsp.shared.springboot.security;

import com.jpriva.erpsp.shared.domain.model.token.access.TokenClaims;
import com.jpriva.erpsp.shared.domain.model.token.access.TokenType;
import com.jpriva.erpsp.shared.domain.ports.out.TokenHandlerPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenHandlerPort tokenHandler;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (token != null) {
            tokenHandler.validateToken(token).ifPresent(claims -> {

                if (claims.type() == TokenType.ACCESS) {
                    setAuthenticationContext(claims, request);
                }
            });
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void setAuthenticationContext(TokenClaims claims, HttpServletRequest request) {
        String roleName = claims.roleName();
        if (roleName != null && !roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        var authorities = roleName != null
                ? List.of(new SimpleGrantedAuthority(roleName))
                : List.<SimpleGrantedAuthority>of();

        AuthenticatedUser principal = new AuthenticatedUser(
                claims.userId(),
                claims.email().value(),
                claims.tenantId(),
                authorities
        );

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                authorities
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
