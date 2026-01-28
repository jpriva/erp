package com.jpriva.erpsp.auth.domain.model.utils;

import com.jpriva.erpsp.auth.domain.model.credential.BiometricType;
import com.jpriva.erpsp.auth.domain.model.credential.OpenIdProvider;
import com.jpriva.erpsp.auth.domain.ports.out.BiometricVerifierPort;
import com.jpriva.erpsp.auth.domain.ports.out.OpenIdTokenValidatorPort;
import com.jpriva.erpsp.auth.domain.ports.out.PasswordHasherPort;

public class PasswordTestUtils {

    public static final PasswordHasherPort fakeHasher = new PasswordHasherPort() {
        @Override
        public String encode(String raw) {
            return "ENCODED_" + raw;
        }

        @Override
        public boolean matches(String raw, String encoded) {
            return encoded.endsWith(raw);
        }
    };

    public static final OpenIdTokenValidatorPort fakeOpenIdValidator = new OpenIdTokenValidatorPort() {
        @Override
        public boolean validate(String idToken, OpenIdProvider expectedProvider, String expectedSubject) {
            return idToken != null && idToken.startsWith("valid_");
        }

        @Override
        public String extractSubject(String idToken, OpenIdProvider provider) {
            if (idToken != null && idToken.startsWith("valid_")) {
                return "extracted-subject";
            }
            return null;
        }
    };

    public static final BiometricVerifierPort fakeBiometricVerifier = new BiometricVerifierPort() {
        @Override
        public boolean verify(byte[] biometricSample, String templateId, BiometricType biometricType) {
            return biometricSample != null && biometricSample.length > 0;
        }

        @Override
        public String enroll(byte[] biometricSample, BiometricType biometricType) {
            return "enrolled-template-" + biometricType.name();
        }
    };
}
