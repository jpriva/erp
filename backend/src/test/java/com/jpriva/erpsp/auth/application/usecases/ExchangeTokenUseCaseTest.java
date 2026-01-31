package com.jpriva.erpsp.auth.application.usecases;

import com.jpriva.erpsp.auth.application.dto.ExchangeTokenCommand;
import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthException;
import com.jpriva.erpsp.auth.domain.model.membership.MembershipRole;
import com.jpriva.erpsp.auth.domain.model.membership.TenantMembership;
import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.auth.domain.model.role.RoleName;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.User;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.model.utils.FakeTokenHandler;
import com.jpriva.erpsp.auth.domain.model.utils.FakeTransactional;
import com.jpriva.erpsp.auth.domain.ports.out.TenantMembershipRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.TransactionalPort;
import com.jpriva.erpsp.auth.domain.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeTokenUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private TenantMembershipRepositoryPort membershipRepository;
    private final TransactionalPort transactional = new FakeTransactional();
    private FakeTokenHandler tokenHandler;

    private ExchangeTokenUseCase useCase;

    @BeforeEach
    void setUp() {
        tokenHandler = new FakeTokenHandler();
        useCase = new ExchangeTokenUseCase(transactional, userRepository, membershipRepository, tokenHandler);
    }

    @Test
    void shouldExchangeTokenSuccessfully() {
        User user = User.create("test@example.com", "John", "Doe");
        TenantId tenantId = TenantId.generate();
        String roleName = "ADMIN";
        String accessToken = tokenHandler.generateAccessToken(user);

        TenantMembership membership = createMembership(user.getUserId(), tenantId, roleName);

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(membershipRepository.findByUserIdAndTenantId(user.getUserId(), tenantId))
                .thenReturn(Optional.of(membership));

        ExchangeTokenCommand cmd = new ExchangeTokenCommand(accessToken, tenantId.toString(), roleName);
        String newToken = useCase.execute(cmd);

        assertThat(newToken).isNotBlank();
        assertThat(newToken).isNotEqualTo(accessToken);

        var claims = tokenHandler.validateToken(newToken);
        assertThat(claims).isPresent();
        assertThat(claims.get().hasTenantContext()).isTrue();
        assertThat(claims.get().tenantId()).isEqualTo(tenantId);
        assertThat(claims.get().roleName()).isEqualTo(roleName);
    }

    @Test
    void shouldFailWhenTokenIsInvalid() {
        ExchangeTokenCommand cmd = new ExchangeTokenCommand("invalid_token", TenantId.generate().toString(), "ADMIN");

        assertThatThrownBy(() -> useCase.execute(cmd))
                .isInstanceOf(ErpAuthException.class)
                .satisfies(ex -> {
                    ErpAuthException authEx = (ErpAuthException) ex;
                    assertThat(authEx.getCode()).isEqualTo(AuthErrorCode.TOKEN_INVALID);
                });
    }

    @Test
    void shouldFailWhenUserNotFound() {
        User user = User.create("test@example.com", "John", "Doe");
        String accessToken = tokenHandler.generateAccessToken(user);

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());

        ExchangeTokenCommand cmd = new ExchangeTokenCommand(accessToken, TenantId.generate().toString(), "ADMIN");

        assertThatThrownBy(() -> useCase.execute(cmd))
                .isInstanceOf(ErpAuthException.class)
                .satisfies(ex -> {
                    ErpAuthException authEx = (ErpAuthException) ex;
                    assertThat(authEx.getCode()).isEqualTo(AuthErrorCode.USER_NOT_FOUND);
                });
    }

    @Test
    void shouldFailWhenMembershipNotFound() {
        User user = User.create("test@example.com", "John", "Doe");
        TenantId tenantId = TenantId.generate();
        String accessToken = tokenHandler.generateAccessToken(user);

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(membershipRepository.findByUserIdAndTenantId(user.getUserId(), tenantId))
                .thenReturn(Optional.empty());

        ExchangeTokenCommand cmd = new ExchangeTokenCommand(accessToken, tenantId.toString(), "ADMIN");

        assertThatThrownBy(() -> useCase.execute(cmd))
                .isInstanceOf(ErpAuthException.class)
                .satisfies(ex -> {
                    ErpAuthException authEx = (ErpAuthException) ex;
                    assertThat(authEx.getCode()).isEqualTo(AuthErrorCode.MEMBERSHIP_NOT_FOUND);
                });
    }

    @Test
    void shouldFailWhenRoleNotAssigned() {
        User user = User.create("test@example.com", "John", "Doe");
        TenantId tenantId = TenantId.generate();
        String accessToken = tokenHandler.generateAccessToken(user);

        TenantMembership membership = createMembership(user.getUserId(), tenantId, "USER");

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(membershipRepository.findByUserIdAndTenantId(user.getUserId(), tenantId))
                .thenReturn(Optional.of(membership));

        ExchangeTokenCommand cmd = new ExchangeTokenCommand(accessToken, tenantId.toString(), "ADMIN");

        assertThatThrownBy(() -> useCase.execute(cmd))
                .isInstanceOf(ErpAuthException.class)
                .satisfies(ex -> {
                    ErpAuthException authEx = (ErpAuthException) ex;
                    assertThat(authEx.getCode()).isEqualTo(AuthErrorCode.ROLE_NOT_ASSIGNED);
                });
    }

    @Test
    void shouldFailWhenUsingRefreshToken() {
        User user = User.create("test@example.com", "John", "Doe");
        String refreshToken = tokenHandler.generateTokens(user).refreshToken();

        ExchangeTokenCommand cmd = new ExchangeTokenCommand(refreshToken, TenantId.generate().toString(), "ADMIN");

        assertThatThrownBy(() -> useCase.execute(cmd))
                .isInstanceOf(ErpAuthException.class)
                .satisfies(ex -> {
                    ErpAuthException authEx = (ErpAuthException) ex;
                    assertThat(authEx.getCode()).isEqualTo(AuthErrorCode.TOKEN_INVALID);
                });
    }

    private TenantMembership createMembership(UserId userId, TenantId tenantId, String roleName) {
        RoleId roleId = RoleId.generate();
        MembershipRole role = MembershipRole.create(roleId, new RoleName(roleName), userId);
        return TenantMembership.create(userId, tenantId, Set.of(role), userId);
    }
}
