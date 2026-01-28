package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.model.utils.PasswordTestUtils;
import com.jpriva.erpsp.shared.domain.exceptions.ErpPersistenceCompromisedException;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordCredentialTest {

    @Nested
    class CreateTests {
        @Test
        void create_Success() {
            UserId userId = UserId.generate();
            PasswordCredential credential = PasswordCredential.create(userId, "validPassword123", PasswordTestUtils.fakeHasher);

            assertThat(credential).isNotNull();
            assertThat(credential.getCredentialId()).isNotNull();
            assertThat(credential.getUserId()).isEqualTo(userId);
            assertThat(credential.getType()).isEqualTo(CredentialType.PASSWORD);
            assertThat(credential.getStatus()).isEqualTo(CredentialStatus.ACTIVE);
            assertThat(credential.getPassword()).isNotNull();
            assertThat(credential.getPassword().hash()).isEqualTo("ENCODED_validPassword123");
            assertThat(credential.getCreatedAt()).isNotNull();
            assertThat(credential.getLastUsedAt()).isNull();
            assertThat(credential.isActive()).isTrue();
        }

        @Test
        void create_FailIfUserIdNull() {
            assertThatThrownBy(() -> PasswordCredential.create(null, "validPassword123", PasswordTestUtils.fakeHasher))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getPlainErrors())
                                .containsKey("userId");
                    });
        }

        @Test
        void create_FailIfPasswordInvalid() {
            UserId userId = UserId.generate();
            assertThatThrownBy(() -> PasswordCredential.create(userId, "short", PasswordTestUtils.fakeHasher))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getPlainErrors())
                                .containsKey("password");
                    });
        }

        @Test
        void create_FailIfBothUserIdNullAndPasswordInvalid() {
            assertThatThrownBy(() -> PasswordCredential.create(null, "short", PasswordTestUtils.fakeHasher))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getPlainErrors())
                                .containsKey("userId")
                                .containsKey("password");
                    });
        }
    }

    @Nested
    class FromPersistenceTests {
        @Test
        void fromPersistence_Success() {
            UUID credentialId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            Instant createdAt = Instant.now().minusSeconds(3600);
            Instant lastUsedAt = Instant.now().minusSeconds(600);

            PasswordCredential credential = PasswordCredential.fromPersistence(
                    credentialId,
                    userId,
                    "stored_hash",
                    "ACTIVE",
                    createdAt,
                    lastUsedAt
            );

            assertThat(credential).isNotNull();
            assertThat(credential.getCredentialId().value()).isEqualTo(credentialId);
            assertThat(credential.getUserId().value()).isEqualTo(userId);
            assertThat(credential.getPassword().hash()).isEqualTo("stored_hash");
            assertThat(credential.getStatus()).isEqualTo(CredentialStatus.ACTIVE);
            assertThat(credential.getCreatedAt()).isEqualTo(createdAt);
            assertThat(credential.getLastUsedAt()).isEqualTo(lastUsedAt);
        }

        @Test
        void fromPersistence_ThrowsPersistenceCompromised_WhenInvalidData() {
            assertThatThrownBy(() -> PasswordCredential.fromPersistence(
                    null,
                    UUID.randomUUID(),
                    "stored_hash",
                    "ACTIVE",
                    Instant.now(),
                    null
            ))
                    .isInstanceOf(ErpPersistenceCompromisedException.class)
                    .satisfies(exception -> {
                        ErpPersistenceCompromisedException ex = (ErpPersistenceCompromisedException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                    });
        }
    }

    @Nested
    class VerifyTests {
        @Test
        void verify_ReturnsTrue_WhenPasswordMatches() {
            UserId userId = UserId.generate();
            PasswordCredential credential = PasswordCredential.create(userId, "validPassword123", PasswordTestUtils.fakeHasher);

            assertThat(credential.verify("validPassword123", PasswordTestUtils.fakeHasher)).isTrue();
        }

        @Test
        void verify_ReturnsFalse_WhenPasswordDoesNotMatch() {
            UserId userId = UserId.generate();
            PasswordCredential credential = PasswordCredential.create(userId, "validPassword123", PasswordTestUtils.fakeHasher);

            assertThat(credential.verify("wrongPassword", PasswordTestUtils.fakeHasher)).isFalse();
        }

        @Test
        void verify_ReturnsFalse_WhenNotActive() {
            UserId userId = UserId.generate();
            PasswordCredential credential = PasswordCredential.create(userId, "validPassword123", PasswordTestUtils.fakeHasher);
            credential.disable();

            assertThat(credential.verify("validPassword123", PasswordTestUtils.fakeHasher)).isFalse();
        }
    }

    @Nested
    class ChangePasswordTests {
        @Test
        void changePassword_Success() {
            UserId userId = UserId.generate();
            PasswordCredential credential = PasswordCredential.create(userId, "validPassword123", PasswordTestUtils.fakeHasher);

            credential.changePassword("newPassword456", PasswordTestUtils.fakeHasher);

            assertThat(credential.getPassword().hash()).isEqualTo("ENCODED_newPassword456");
            assertThat(credential.verify("newPassword456", PasswordTestUtils.fakeHasher)).isTrue();
            assertThat(credential.verify("validPassword123", PasswordTestUtils.fakeHasher)).isFalse();
        }

        @Test
        void changePassword_FailIfNewPasswordInvalid() {
            UserId userId = UserId.generate();
            PasswordCredential credential = PasswordCredential.create(userId, "validPassword123", PasswordTestUtils.fakeHasher);

            assertThatThrownBy(() -> credential.changePassword("short", PasswordTestUtils.fakeHasher))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getPlainErrors())
                                .containsKey("password");
                    });
        }
    }

    @Nested
    class StatusTests {
        @Test
        void disable_SetsStatusToDisabled() {
            UserId userId = UserId.generate();
            PasswordCredential credential = PasswordCredential.create(userId, "validPassword123", PasswordTestUtils.fakeHasher);

            credential.disable();

            assertThat(credential.getStatus()).isEqualTo(CredentialStatus.DISABLED);
            assertThat(credential.isActive()).isFalse();
        }

        @Test
        void activate_SetsStatusToActive() {
            UserId userId = UserId.generate();
            PasswordCredential credential = PasswordCredential.create(userId, "validPassword123", PasswordTestUtils.fakeHasher);
            credential.disable();

            credential.activate();

            assertThat(credential.getStatus()).isEqualTo(CredentialStatus.ACTIVE);
            assertThat(credential.isActive()).isTrue();
        }

        @Test
        void markAsCompromised_SetsStatusToCompromised() {
            UserId userId = UserId.generate();
            PasswordCredential credential = PasswordCredential.create(userId, "validPassword123", PasswordTestUtils.fakeHasher);

            credential.markAsCompromised();

            assertThat(credential.getStatus()).isEqualTo(CredentialStatus.COMPROMISED);
            assertThat(credential.isActive()).isFalse();
        }

        @Test
        void markAsExpired_SetsStatusToExpired() {
            UserId userId = UserId.generate();
            PasswordCredential credential = PasswordCredential.create(userId, "validPassword123", PasswordTestUtils.fakeHasher);

            credential.markAsExpired();

            assertThat(credential.getStatus()).isEqualTo(CredentialStatus.EXPIRED);
            assertThat(credential.isActive()).isFalse();
        }

        @Test
        void recordUsage_SetsLastUsedAt() {
            UserId userId = UserId.generate();
            PasswordCredential credential = PasswordCredential.create(userId, "validPassword123", PasswordTestUtils.fakeHasher);
            assertThat(credential.getLastUsedAt()).isNull();

            credential.recordUsage();

            assertThat(credential.getLastUsedAt()).isNotNull();
        }
    }
}
