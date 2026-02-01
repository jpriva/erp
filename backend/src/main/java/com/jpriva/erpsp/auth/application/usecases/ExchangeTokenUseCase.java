package com.jpriva.erpsp.auth.application.usecases;

import com.jpriva.erpsp.auth.application.dto.ExchangeTokenCommand;
import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthException;
import com.jpriva.erpsp.auth.domain.model.membership.MembershipRole;
import com.jpriva.erpsp.auth.domain.model.membership.TenantMembership;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.token.TokenClaims;
import com.jpriva.erpsp.auth.domain.model.user.User;
import com.jpriva.erpsp.auth.domain.ports.out.TenantMembershipRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.TokenHandlerPort;
import com.jpriva.erpsp.auth.domain.ports.out.TransactionalPort;
import com.jpriva.erpsp.auth.domain.ports.out.UserRepositoryPort;

/**
 * Use case for exchanging a base access token for one with tenant/role context.
 * <p>
 * Flow:
 * 1. Validate the current access token
 * 2. Verify user has an active membership in the requested tenant
 * 3. Verify user has the requested role in that membership
 * 4. Generate new access token with tenant/role context
 */
public class ExchangeTokenUseCase {

    private final TransactionalPort transactionalPort;
    private final UserRepositoryPort userRepository;
    private final TenantMembershipRepositoryPort membershipRepository;
    private final TokenHandlerPort tokenHandler;

    public ExchangeTokenUseCase(
            TransactionalPort transactionalPort,
            UserRepositoryPort userRepository,
            TenantMembershipRepositoryPort membershipRepository,
            TokenHandlerPort tokenHandler
    ) {
        this.transactionalPort = transactionalPort;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.tokenHandler = tokenHandler;
    }

    public String execute(ExchangeTokenCommand cmd) {
        return transactionalPort.executeReadOnly(() -> {
            TokenClaims claims = tokenHandler.validateToken(cmd.accessToken())
                    .filter(TokenClaims::isAccessToken)
                    .orElseThrow(() -> new ErpAuthException(AuthErrorCode.TOKEN_INVALID));

            User user = userRepository.findById(claims.userId())
                    .orElseThrow(() -> new ErpAuthException(AuthErrorCode.USER_NOT_FOUND));

            TenantId tenantId = TenantId.from(cmd.tenantId());

            TenantMembership membership = membershipRepository
                    .findByUserIdAndTenantId(claims.userId(), tenantId)
                    .filter(TenantMembership::isActive)
                    .orElseThrow(() -> new ErpAuthException(AuthErrorCode.MEMBERSHIP_NOT_FOUND));

            boolean hasRole = membership.getRoles().stream()
                    .map(MembershipRole::roleName)
                    .anyMatch(rn -> rn.value().equals(cmd.roleName()));

            if (!hasRole) {
                throw new ErpAuthException(AuthErrorCode.ROLE_NOT_ASSIGNED);
            }

            return tokenHandler.generateAccessTokenWithContext(user, tenantId, cmd.roleName());
        });
    }
}
