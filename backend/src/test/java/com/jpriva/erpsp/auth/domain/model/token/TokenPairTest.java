package com.jpriva.erpsp.auth.domain.model.token;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenPairTest {

    @Test
    void shouldCreateTokenPair() {
        TokenPair pair = new TokenPair(
                "accessToken123",
                "refreshToken456",
                900,
                604800
        );

        assertThat(pair.accessToken()).isEqualTo("accessToken123");
        assertThat(pair.refreshToken()).isEqualTo("refreshToken456");
        assertThat(pair.accessTokenExpiresIn()).isEqualTo(900);
        assertThat(pair.refreshTokenExpiresIn()).isEqualTo(604800);
    }
}
