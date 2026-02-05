package com.jpriva.erpsp.auth.application.usecases;

import com.jpriva.erpsp.auth.application.dto.TokenView;
import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthException;
import com.jpriva.erpsp.auth.domain.model.user.User;
import com.jpriva.erpsp.auth.domain.ports.out.UserRepositoryPort;
import com.jpriva.erpsp.shared.domain.ports.out.TransactionalPort;
import com.jpriva.erpsp.utils.FakeTokenHandler;
import com.jpriva.erpsp.utils.FakeTransactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;
    private final TransactionalPort transactional = new FakeTransactional();
    private FakeTokenHandler tokenHandler;

    private RefreshTokenUseCase useCase;

    @BeforeEach
    void setUp() {
        tokenHandler = new FakeTokenHandler();
        useCase = new RefreshTokenUseCase(transactional, userRepository, tokenHandler);
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        User user = User.create("test@example.com", "John", "Doe");
        String refreshToken = tokenHandler.generateTokens(user.getUserId(), user.getEmail()).refreshToken();

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        TokenView response = useCase.execute(refreshToken);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.refreshToken()).isEqualTo(refreshToken);
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void shouldFailWhenRefreshTokenIsInvalid() {
        assertThatThrownBy(() -> useCase.execute("invalid_refresh_token"))
                .isInstanceOf(ErpAuthException.class)
                .satisfies(ex -> {
                    ErpAuthException authEx = (ErpAuthException) ex;
                    assertThat(authEx.getCode()).isEqualTo(AuthErrorCode.TOKEN_INVALID);
                });
    }

    @Test
    void shouldFailWhenUsingAccessTokenAsRefresh() {
        User user = User.create("test@example.com", "John", "Doe");
        String accessToken = tokenHandler.generateAccessToken(user.getUserId(), user.getEmail());

        assertThatThrownBy(() -> useCase.execute(accessToken))
                .isInstanceOf(ErpAuthException.class)
                .satisfies(ex -> {
                    ErpAuthException authEx = (ErpAuthException) ex;
                    assertThat(authEx.getCode()).isEqualTo(AuthErrorCode.TOKEN_INVALID);
                });
    }

    @Test
    void shouldFailWhenUserNotFound() {
        User user = User.create("test@example.com", "John", "Doe");
        String refreshToken = tokenHandler.generateTokens(user.getUserId(), user.getEmail()).refreshToken();

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(refreshToken))
                .isInstanceOf(ErpAuthException.class)
                .satisfies(ex -> {
                    ErpAuthException authEx = (ErpAuthException) ex;
                    assertThat(authEx.getCode()).isEqualTo(AuthErrorCode.USER_NOT_FOUND);
                });
    }
}
