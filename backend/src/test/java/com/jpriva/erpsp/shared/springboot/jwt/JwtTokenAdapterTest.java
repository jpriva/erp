package com.jpriva.erpsp.shared.springboot.jwt;

import com.jpriva.erpsp.auth.domain.model.user.User;
import com.jpriva.erpsp.shared.domain.model.TenantId;
import com.jpriva.erpsp.shared.domain.model.token.access.TokenClaims;
import com.jpriva.erpsp.shared.domain.model.token.access.TokenPair;
import com.jpriva.erpsp.shared.domain.model.token.access.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenAdapterTest {

    private JwtTokenAdapter adapter;

    @BeforeEach
    void setUp() {
        JwtConfig config = new JwtConfig();
        config.setSecret("this-is-a-very-secure-secret-key-for-testing-purposes-only");
        config.setAccessTokenExpiration(900);
        config.setRefreshTokenExpiration(604800);
        adapter = new JwtTokenAdapter(config);
    }

    private User createTestUser() {
        return User.create("test@example.com", "John", "Doe");
    }

    @Nested
    class GenerateTokensTests {

        @Test
        void shouldGenerateTokenPair() {
            User user = createTestUser();

            TokenPair pair = adapter.generateTokens(user.getUserId(), user.getEmail());

            assertThat(pair).isNotNull();
            assertThat(pair.accessToken()).isNotBlank();
            assertThat(pair.refreshToken()).isNotBlank();
            assertThat(pair.accessTokenExpiresIn()).isEqualTo(900);
            assertThat(pair.refreshTokenExpiresIn()).isEqualTo(604800);
        }

        @Test
        void shouldGenerateValidAccessToken() {
            User user = createTestUser();

            String token = adapter.generateAccessToken(user.getUserId(), user.getEmail());
            Optional<TokenClaims> claims = adapter.validateToken(token);

            assertThat(claims).isPresent();
            assertThat(claims.get().isAccessToken()).isTrue();
            assertThat(claims.get().userId()).isEqualTo(user.getUserId());
            assertThat(claims.get().email().value()).isEqualTo(user.getEmail().value());
            assertThat(claims.get().hasTenantContext()).isFalse();
        }

        @Test
        void shouldGenerateAccessTokenWithContext() {
            User user = createTestUser();
            TenantId tenantId = TenantId.generate();
            String roleName = "ADMIN";

            String token = adapter.generateAccessTokenWithContext(user.getUserId(), user.getEmail(), tenantId, roleName);
            Optional<TokenClaims> claims = adapter.validateToken(token);

            assertThat(claims).isPresent();
            assertThat(claims.get().isAccessToken()).isTrue();
            assertThat(claims.get().hasTenantContext()).isTrue();
            assertThat(claims.get().tenantId()).isEqualTo(tenantId);
            assertThat(claims.get().roleName()).isEqualTo(roleName);
        }

        @Test
        void shouldGenerateValidRefreshToken() {
            User user = createTestUser();

            TokenPair pair = adapter.generateTokens(user.getUserId(), user.getEmail());
            Optional<TokenClaims> claims = adapter.validateRefreshToken(pair.refreshToken());

            assertThat(claims).isPresent();
            assertThat(claims.get().isRefreshToken()).isTrue();
            assertThat(claims.get().userId()).isEqualTo(user.getUserId());
            assertThat(claims.get().email()).isNull();
            assertThat(claims.get().hasTenantContext()).isFalse();
        }
    }

    @Nested
    class ValidateTokenTests {

        @Test
        void shouldReturnEmptyForInvalidToken() {
            Optional<TokenClaims> claims = adapter.validateToken("invalid.token.here");

            assertThat(claims).isEmpty();
        }

        @Test
        void shouldReturnEmptyForNullToken() {
            Optional<TokenClaims> claims = adapter.validateToken(null);

            assertThat(claims).isEmpty();
        }

        @Test
        void shouldReturnEmptyForEmptyToken() {
            Optional<TokenClaims> claims = adapter.validateToken("");

            assertThat(claims).isEmpty();
        }

        @Test
        void shouldReturnEmptyWhenValidatingAccessTokenAsRefresh() {
            User user = createTestUser();
            String accessToken = adapter.generateAccessToken(user.getUserId(), user.getEmail());

            Optional<TokenClaims> claims = adapter.validateRefreshToken(accessToken);

            assertThat(claims).isEmpty();
        }
    }

    @Nested
    class TokenTypeTests {

        @Test
        void accessTokenShouldHaveCorrectType() {
            User user = createTestUser();
            String token = adapter.generateAccessToken(user.getUserId(), user.getEmail());
            Optional<TokenClaims> claims = adapter.validateToken(token);

            assertThat(claims).isPresent();
            assertThat(claims.get().type()).isEqualTo(TokenType.ACCESS);
        }

        @Test
        void refreshTokenShouldHaveCorrectType() {
            User user = createTestUser();
            TokenPair pair = adapter.generateTokens(user.getUserId(), user.getEmail());
            Optional<TokenClaims> claims = adapter.validateToken(pair.refreshToken());

            assertThat(claims).isPresent();
            assertThat(claims.get().type()).isEqualTo(TokenType.REFRESH);
        }
    }
}
