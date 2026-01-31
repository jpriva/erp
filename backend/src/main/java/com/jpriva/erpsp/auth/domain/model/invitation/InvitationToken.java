package com.jpriva.erpsp.auth.domain.model.invitation;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.constants.InvitationValidationError;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cryptographically secure token for invitation acceptance.
 * Generated using SecureRandom with 256-bit entropy (64 Base64 characters).
 */
public record InvitationToken(String value) {
    private static final int TOKEN_BYTES = 48; // 48 bytes * 8/6 = 64 Base64 characters
    private static final int TOKEN_CHARS = 64;

    public InvitationToken {
        var val = ValidationError.builder();
        if (value == null || value.isBlank()) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(InvitationValidationError.TOKEN_EMPTY).build()
            );
        }
        if (value.length() != TOKEN_CHARS) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(InvitationValidationError.TOKEN_FORMAT).build()
            );
        }
    }

    /**
     * Generates a new cryptographically secure invitation token.
     * Token is 64 Base64 characters representing 48 bytes (256 bits of entropy).
     */
    public static InvitationToken generate() {
        byte[] randomBytes = new byte[TOKEN_BYTES];
        new SecureRandom().nextBytes(randomBytes);
        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
        if (token.length() > TOKEN_CHARS) {
            token = token.substring(0, TOKEN_CHARS);
        }
        return new InvitationToken(token);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return value;
    }
}
