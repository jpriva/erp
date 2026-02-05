package com.jpriva.erpsp.shared.springboot.jwt;

import com.jpriva.erpsp.shared.domain.model.Email;
import com.jpriva.erpsp.shared.domain.model.TenantId;
import com.jpriva.erpsp.shared.domain.model.UserId;
import com.jpriva.erpsp.shared.domain.model.token.access.TokenClaims;
import com.jpriva.erpsp.shared.domain.model.token.access.TokenPair;
import com.jpriva.erpsp.shared.domain.model.token.access.TokenType;
import com.jpriva.erpsp.shared.domain.ports.out.TokenHandlerPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtTokenAdapter implements TokenHandlerPort {

    private final JwtConfig config;

    @Override
    public TokenPair generateTokens(UserId userId, Email email) {
        String accessToken = generateAccessToken(userId, email);
        String refreshToken = generateRefreshToken(userId);
        return new TokenPair(
                accessToken,
                refreshToken,
                config.getAccessTokenExpiration(),
                config.getRefreshTokenExpiration()
        );
    }

    @Override
    public String generateAccessToken(UserId userId, Email email) {
        return buildAccessToken(userId, email, null, null);
    }

    @Override
    public String generateAccessTokenWithContext(UserId userId, Email email, TenantId tenantId, String roleName) {
        return buildAccessToken(userId, email, tenantId, roleName);
    }

    private String buildAccessToken(UserId userId, Email email, TenantId tenantId, String roleName) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(config.getAccessTokenExpiration());

        var builder = Jwts.builder()
                .subject(userId.toString())
                .claim("email", email.value())
                .claim("type", TokenType.ACCESS.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry));

        if (tenantId != null) {
            builder.claim("tenantId", tenantId.toString());
        }
        if (roleName != null) {
            builder.claim("roleName", roleName);
        }

        return builder.signWith(getSigningKey()).compact();
    }

    private String generateRefreshToken(UserId userId) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(config.getRefreshTokenExpiration());

        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", TokenType.REFRESH.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public Optional<TokenClaims> validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return Optional.of(extractClaims(claims));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<TokenClaims> validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken)
                .filter(TokenClaims::isRefreshToken);
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private TokenClaims extractClaims(Claims claims) {
        String typeStr = claims.get("type", String.class);
        TokenType type = TokenType.valueOf(typeStr);

        String emailStr = claims.get("email", String.class);
        Email email = emailStr != null ? new Email(emailStr) : null;

        String tenantIdStr = claims.get("tenantId", String.class);
        TenantId tenantId = tenantIdStr != null ? TenantId.from(tenantIdStr) : null;

        String roleName = claims.get("roleName", String.class);

        return new TokenClaims(
                UserId.from(claims.getSubject()),
                email,
                type,
                tenantId,
                roleName,
                claims.getIssuedAt().toInstant(),
                claims.getExpiration().toInstant()
        );
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(config.getSecret().getBytes());
    }
}
