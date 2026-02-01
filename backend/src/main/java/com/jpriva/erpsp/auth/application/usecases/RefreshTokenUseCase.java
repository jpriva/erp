package com.jpriva.erpsp.auth.application.usecases;

import com.jpriva.erpsp.auth.application.dto.TokenView;
import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthException;
import com.jpriva.erpsp.auth.domain.model.token.TokenClaims;
import com.jpriva.erpsp.auth.domain.model.user.User;
import com.jpriva.erpsp.auth.domain.ports.out.TokenHandlerPort;
import com.jpriva.erpsp.auth.domain.ports.out.TransactionalPort;
import com.jpriva.erpsp.auth.domain.ports.out.UserRepositoryPort;

public class RefreshTokenUseCase {

    private final TransactionalPort transactionalPort;
    private final UserRepositoryPort userRepository;
    private final TokenHandlerPort tokenHandler;

    public RefreshTokenUseCase(
            TransactionalPort transactionalPort,
            UserRepositoryPort userRepository,
            TokenHandlerPort tokenHandler
    ) {
        this.transactionalPort = transactionalPort;
        this.userRepository = userRepository;
        this.tokenHandler = tokenHandler;
    }

    public TokenView execute(String refreshToken) {
        return transactionalPort.executeReadOnly(() -> {
            TokenClaims claims = tokenHandler.validateRefreshToken(refreshToken)
                    .orElseThrow(() -> new ErpAuthException(AuthErrorCode.TOKEN_INVALID));

            User user = userRepository.findById(claims.userId())
                    .orElseThrow(() -> new ErpAuthException(AuthErrorCode.USER_NOT_FOUND));

            String newAccessToken = tokenHandler.generateAccessToken(user);

            return new TokenView(
                    newAccessToken,
                    "Bearer",
                    900,
                    refreshToken,
                    "read write"
            );
        });
    }
}
