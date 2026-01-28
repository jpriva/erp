package com.jpriva.erpsp.auth.domain.ports.out;

import com.jpriva.erpsp.auth.domain.model.credential.OpenIdProvider;

/**
 * Port for validating OpenID Connect tokens.
 */
public interface OpenIdTokenValidatorPort {

    /**
     * Validates an ID token from an OpenID provider.
     *
     * @param idToken         the ID token to validate
     * @param expectedProvider the expected OpenID provider
     * @param expectedSubject  the expected subject claim
     * @return true if the token is valid and matches the expected provider and subject
     */
    boolean validate(String idToken, OpenIdProvider expectedProvider, String expectedSubject);

    /**
     * Extracts the subject claim from an ID token.
     *
     * @param idToken  the ID token
     * @param provider the OpenID provider
     * @return the subject claim value, or null if invalid
     */
    String extractSubject(String idToken, OpenIdProvider provider);
}
