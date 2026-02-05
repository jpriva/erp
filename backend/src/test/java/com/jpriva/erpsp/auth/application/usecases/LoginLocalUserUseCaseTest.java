package com.jpriva.erpsp.auth.application.usecases;

import com.jpriva.erpsp.auth.application.dto.LocalLoginCommand;
import com.jpriva.erpsp.auth.application.dto.TokenView;
import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthException;
import com.jpriva.erpsp.auth.domain.model.credential.PasswordCredential;
import com.jpriva.erpsp.auth.domain.model.user.User;
import com.jpriva.erpsp.auth.domain.ports.out.CredentialRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.PasswordHasherPort;
import com.jpriva.erpsp.shared.domain.ports.out.TokenHandlerPort;
import com.jpriva.erpsp.auth.domain.ports.out.UserRepositoryPort;
import com.jpriva.erpsp.shared.domain.model.Email;
import com.jpriva.erpsp.shared.domain.ports.out.TransactionalPort;
import com.jpriva.erpsp.utils.FakePasswordHasher;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginLocalUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private CredentialRepositoryPort credentialRepository;
    private final TransactionalPort transactional = new FakeTransactional();
    private final PasswordHasherPort passwordHasher = new FakePasswordHasher();
    private final TokenHandlerPort tokenHandler = new FakeTokenHandler();

    private LoginLocalUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new LoginLocalUserUseCase(
                transactional,
                userRepository,
                credentialRepository,
                passwordHasher,
                tokenHandler
        );
    }

    @Test
    void shouldLoginSuccessfully() {
        User user = User.create("test@example.com", "John", "Doe");
        PasswordCredential credential = PasswordCredential.create(user.getUserId(), "password123", passwordHasher);

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(credentialRepository.findPasswordByUserId(user.getUserId())).thenReturn(Optional.of(credential));

        LocalLoginCommand cmd = new LocalLoginCommand("test@example.com", "password123");
        TokenView response = useCase.execute(cmd);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isGreaterThan(0);

        verify(credentialRepository, times(1)).save(any(PasswordCredential.class));
    }

    @Test
    void shouldFailWhenUserNotFound() {
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        LocalLoginCommand cmd = new LocalLoginCommand("nonexistent@example.com", "password123");

        assertThatThrownBy(() -> useCase.execute(cmd))
                .isInstanceOf(ErpAuthException.class)
                .satisfies(ex -> {
                    ErpAuthException authEx = (ErpAuthException) ex;
                    assertThat(authEx.getCode()).isEqualTo(AuthErrorCode.CREDENTIAL_INVALID);
                });
    }

    @Test
    void shouldFailWhenCredentialNotFound() {
        User user = User.create("test@example.com", "John", "Doe");

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(credentialRepository.findPasswordByUserId(user.getUserId())).thenReturn(Optional.empty());

        LocalLoginCommand cmd = new LocalLoginCommand("test@example.com", "password123");

        assertThatThrownBy(() -> useCase.execute(cmd))
                .isInstanceOf(ErpAuthException.class)
                .satisfies(ex -> {
                    ErpAuthException authEx = (ErpAuthException) ex;
                    assertThat(authEx.getCode()).isEqualTo(AuthErrorCode.CREDENTIAL_INVALID);
                });
    }

    @Test
    void shouldFailWhenPasswordIsIncorrect() {
        User user = User.create("test@example.com", "John", "Doe");
        PasswordCredential credential = PasswordCredential.create(user.getUserId(), "correctpassword", passwordHasher);

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(credentialRepository.findPasswordByUserId(user.getUserId())).thenReturn(Optional.of(credential));

        LocalLoginCommand cmd = new LocalLoginCommand("test@example.com", "wrongpassword");

        assertThatThrownBy(() -> useCase.execute(cmd))
                .isInstanceOf(ErpAuthException.class)
                .satisfies(ex -> {
                    ErpAuthException authEx = (ErpAuthException) ex;
                    assertThat(authEx.getCode()).isEqualTo(AuthErrorCode.CREDENTIAL_INVALID);
                });
    }
}
