package com.jpriva.erpsp.auth.domain.model.token;

import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.shared.domain.model.Email;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TokenClaimsTest {

    @Nested
    class AccessTokenTests {

        @Test
        void shouldIdentifyAccessToken() {
            TokenClaims claims = new TokenClaims(
                    UserId.generate(),
                    new Email("test@example.com"),
                    TokenType.ACCESS,
                    null,
                    null,
                    Instant.now(),
                    Instant.now().plusSeconds(900)
            );

            assertThat(claims.isAccessToken()).isTrue();
            assertThat(claims.isRefreshToken()).isFalse();
        }

        @Test
        void shouldNotHaveTenantContextWhenNull() {
            TokenClaims claims = new TokenClaims(
                    UserId.generate(),
                    new Email("test@example.com"),
                    TokenType.ACCESS,
                    null,
                    null,
                    Instant.now(),
                    Instant.now().plusSeconds(900)
            );

            assertThat(claims.hasTenantContext()).isFalse();
            assertThat(claims.getTenantId()).isEmpty();
            assertThat(claims.getRoleName()).isEmpty();
        }

        @Test
        void shouldHaveTenantContextWhenBothPresent() {
            TokenClaims claims = new TokenClaims(
                    UserId.generate(),
                    new Email("test@example.com"),
                    TokenType.ACCESS,
                    TenantId.generate(),
                    "ADMIN",
                    Instant.now(),
                    Instant.now().plusSeconds(900)
            );

            assertThat(claims.hasTenantContext()).isTrue();
            assertThat(claims.getTenantId()).isPresent();
            assertThat(claims.getRoleName()).contains("ADMIN");
        }

        @Test
        void shouldNotHaveTenantContextWhenOnlyTenantIdPresent() {
            TokenClaims claims = new TokenClaims(
                    UserId.generate(),
                    new Email("test@example.com"),
                    TokenType.ACCESS,
                    TenantId.generate(),
                    null,
                    Instant.now(),
                    Instant.now().plusSeconds(900)
            );

            assertThat(claims.hasTenantContext()).isFalse();
        }

        @Test
        void shouldNotHaveTenantContextWhenOnlyRoleNamePresent() {
            TokenClaims claims = new TokenClaims(
                    UserId.generate(),
                    new Email("test@example.com"),
                    TokenType.ACCESS,
                    null,
                    "ADMIN",
                    Instant.now(),
                    Instant.now().plusSeconds(900)
            );

            assertThat(claims.hasTenantContext()).isFalse();
        }
    }

    @Nested
    class RefreshTokenTests {

        @Test
        void shouldIdentifyRefreshToken() {
            TokenClaims claims = new TokenClaims(
                    UserId.generate(),
                    null,
                    TokenType.REFRESH,
                    null,
                    null,
                    Instant.now(),
                    Instant.now().plusSeconds(604800)
            );

            assertThat(claims.isRefreshToken()).isTrue();
            assertThat(claims.isAccessToken()).isFalse();
        }
    }

    @Nested
    class ExpirationTests {

        @Test
        void shouldNotBeExpiredWhenExpiresAtIsInFuture() {
            TokenClaims claims = new TokenClaims(
                    UserId.generate(),
                    new Email("test@example.com"),
                    TokenType.ACCESS,
                    null,
                    null,
                    Instant.now(),
                    Instant.now().plusSeconds(900)
            );

            assertThat(claims.isExpired()).isFalse();
        }

        @Test
        void shouldBeExpiredWhenExpiresAtIsInPast() {
            TokenClaims claims = new TokenClaims(
                    UserId.generate(),
                    new Email("test@example.com"),
                    TokenType.ACCESS,
                    null,
                    null,
                    Instant.now().minusSeconds(1000),
                    Instant.now().minusSeconds(100)
            );

            assertThat(claims.isExpired()).isTrue();
        }
    }
}
