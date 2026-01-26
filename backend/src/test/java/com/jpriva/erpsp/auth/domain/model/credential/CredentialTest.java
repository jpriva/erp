package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.model.utils.PasswordTestUtils;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CredentialTest {

    @Test
    void constructor_CreateCredential_Success() {
        CredentialId credentialId = CredentialId.generate();
        UserId userId = UserId.generate();
        AuthProvider provider = AuthProvider.LOCAL;
        String identifier = "username";
        Password secret = Password.create("longPassword", PasswordTestUtils.fakeHasher);
        Credential credential = new Credential(credentialId, userId, provider, identifier, secret);
        assertNotNull(credential);
        assertNotNull(credential.credentialId());
        assertNotNull(credential.userId());
        assertNotNull(credential.provider());
        assertNotNull(credential.identifier());
        assertNotNull(credential.secret());
    }

    @Test
    void constructor_ShouldThrowWhenLocalProviderAndNoPass() {
        CredentialId credentialId = CredentialId.generate();
        UserId userId = UserId.generate();
        AuthProvider provider = AuthProvider.LOCAL;
        String identifier = "username";
        assertThatThrownBy(() -> new Credential(credentialId, userId, provider, identifier, null))
                .isInstanceOf(ErpValidationException.class)
                .satisfies(exception -> {
                    ErpValidationException ex = (ErpValidationException) exception;
                    ErpExceptionTestUtils.printExceptionDetails(ex);
                    assertThat(ex.getModule()).isEqualTo("AUTH");
                    assertThat(ex.getCode()).isNotNull();
                    assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                });
    }

}