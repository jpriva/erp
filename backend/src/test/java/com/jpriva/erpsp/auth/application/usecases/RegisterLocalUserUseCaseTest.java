package com.jpriva.erpsp.auth.application.usecases;

import com.jpriva.erpsp.auth.application.dto.RegisterUserCommand;
import com.jpriva.erpsp.auth.application.dto.UserView;
import com.jpriva.erpsp.auth.domain.model.credential.Credential;
import com.jpriva.erpsp.auth.domain.model.user.User;
import com.jpriva.erpsp.auth.domain.ports.out.*;
import com.jpriva.erpsp.shared.domain.ports.out.LoggerPort;
import com.jpriva.erpsp.shared.domain.ports.out.TransactionalPort;
import com.jpriva.erpsp.utils.FakeLogger;
import com.jpriva.erpsp.utils.FakePasswordHasher;
import com.jpriva.erpsp.utils.FakeTransactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterLocalUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private CredentialRepositoryPort credentialRepository;
    private final TransactionalPort transactional = new FakeTransactional();
    @Spy
    private LoggerPort log = new FakeLogger();
    private final PasswordHasherPort passwordHasher = new FakePasswordHasher();
    @Mock
    private AuthEventPort eventPort;
    @Mock
    private VerificationTokenRepositoryPort verificationTokenRepository;

    private RegisterLocalUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterLocalUserUseCase(
                userRepository,
                credentialRepository,
                transactional,
                log,
                passwordHasher,
                eventPort,
                verificationTokenRepository
        );
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        String email = "success@example.com";
        String firstName = "Success";
        String lastName = "Example";
        String password = "password";
        RegisterUserCommand newUser = new RegisterUserCommand(email, firstName, lastName);

        UserView response = useCase.execute(newUser, password, null);

        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.email()).isEqualTo(email);
        assertThat(response.name()).contains(firstName);
        assertThat(response.name()).contains(lastName);

        verify(userRepository, times(1)).save(any(User.class));
        verify(credentialRepository, times(1)).save(any(Credential.class));
    }
}