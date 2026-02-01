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

import static com.jpriva.erpsp.shared.domain.utils.ValidationErrorAssertions.assertHasFieldError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OpenIdCredentialTest {

    @Nested
    class CreateTests {
        @Test
        void create_Success() {
            UserId userId = UserId.generate();
            OpenIdCredential credential = OpenIdCredential.create(userId, OpenIdProvider.GOOGLE, "google-user-123456");

            assertThat(credential).isNotNull();
            assertThat(credential.getCredentialId()).isNotNull();
            assertThat(credential.getUserId()).isEqualTo(userId);
            assertThat(credential.getType()).isEqualTo(CredentialType.OPENID);
            assertThat(credential.getStatus()).isEqualTo(CredentialStatus.ACTIVE);
            assertThat(credential.getProvider()).isEqualTo(OpenIdProvider.GOOGLE);
            assertThat(credential.getSubject().value()).isEqualTo("google-user-123456");
            assertThat(credential.getCreatedAt()).isNotNull();
            assertThat(credential.getLastUsedAt()).isNull();
            assertThat(credential.isActive()).isTrue();
        }

        @Test
        void create_FailIfUserIdNull() {
            assertThatThrownBy(() -> OpenIdCredential.create(null, OpenIdProvider.GOOGLE, "google-user-123456"))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "userId");
                    });
        }

        @Test
        void create_FailIfProviderNull() {
            UserId userId = UserId.generate();
            assertThatThrownBy(() -> OpenIdCredential.create(userId, null, "google-user-123456"))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "openIdProvider");
                    });
        }

        @Test
        void create_FailIfSubjectNullOrBlank() {
            UserId userId = UserId.generate();
            assertThatThrownBy(() -> OpenIdCredential.create(userId, OpenIdProvider.GOOGLE, null))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "openIdSubject");
                    });
        }

        @Test
        void create_FailIfMultipleFieldsInvalid() {
            assertThatThrownBy(() -> OpenIdCredential.create(null, null, null))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "userId");
                        assertHasFieldError(ex, "openIdProvider");
                        assertHasFieldError(ex, "openIdSubject");
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

            OpenIdCredential credential = OpenIdCredential.fromPersistence(
                    credentialId,
                    userId,
                    "GOOGLE",
                    "google-user-123456",
                    "ACTIVE",
                    createdAt,
                    lastUsedAt
            );

            assertThat(credential).isNotNull();
            assertThat(credential.getCredentialId().value()).isEqualTo(credentialId);
            assertThat(credential.getUserId().value()).isEqualTo(userId);
            assertThat(credential.getProvider()).isEqualTo(OpenIdProvider.GOOGLE);
            assertThat(credential.getSubject().value()).isEqualTo("google-user-123456");
            assertThat(credential.getStatus()).isEqualTo(CredentialStatus.ACTIVE);
            assertThat(credential.getCreatedAt()).isEqualTo(createdAt);
            assertThat(credential.getLastUsedAt()).isEqualTo(lastUsedAt);
        }

        @Test
        void fromPersistence_ThrowsPersistenceCompromised_WhenInvalidData() {
            assertThatThrownBy(() -> OpenIdCredential.fromPersistence(
                    null,
                    UUID.randomUUID(),
                    "GOOGLE",
                    "google-user-123456",
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
        void verify_ReturnsTrue_WhenTokenIsValid() {
            UserId userId = UserId.generate();
            OpenIdCredential credential = OpenIdCredential.create(userId, OpenIdProvider.GOOGLE, "google-user-123456");

            boolean result = credential.verify("valid_token", PasswordTestUtils.fakeOpenIdValidator);

            assertThat(result).isTrue();
        }

        @Test
        void verify_ReturnsFalse_WhenTokenIsInvalid() {
            UserId userId = UserId.generate();
            OpenIdCredential credential = OpenIdCredential.create(userId, OpenIdProvider.GOOGLE, "google-user-123456");

            boolean result = credential.verify("invalid_token", PasswordTestUtils.fakeOpenIdValidator);

            assertThat(result).isFalse();
        }

        @Test
        void verify_ReturnsFalse_WhenNotActive() {
            UserId userId = UserId.generate();
            OpenIdCredential credential = OpenIdCredential.create(userId, OpenIdProvider.GOOGLE, "google-user-123456");
            credential.disable();

            boolean result = credential.verify("valid_token", PasswordTestUtils.fakeOpenIdValidator);

            assertThat(result).isFalse();
        }
    }

    @Nested
    class StatusTests {
        @Test
        void disable_SetsStatusToDisabled() {
            UserId userId = UserId.generate();
            OpenIdCredential credential = OpenIdCredential.create(userId, OpenIdProvider.GOOGLE, "google-user-123456");

            credential.disable();

            assertThat(credential.getStatus()).isEqualTo(CredentialStatus.DISABLED);
            assertThat(credential.isActive()).isFalse();
        }

        @Test
        void activate_SetsStatusToActive() {
            UserId userId = UserId.generate();
            OpenIdCredential credential = OpenIdCredential.create(userId, OpenIdProvider.GOOGLE, "google-user-123456");
            credential.disable();

            credential.activate();

            assertThat(credential.getStatus()).isEqualTo(CredentialStatus.ACTIVE);
            assertThat(credential.isActive()).isTrue();
        }

        @Test
        void markAsCompromised_SetsStatusToCompromised() {
            UserId userId = UserId.generate();
            OpenIdCredential credential = OpenIdCredential.create(userId, OpenIdProvider.GOOGLE, "google-user-123456");

            credential.markAsCompromised();

            assertThat(credential.getStatus()).isEqualTo(CredentialStatus.COMPROMISED);
            assertThat(credential.isActive()).isFalse();
        }

        @Test
        void recordUsage_SetsLastUsedAt() {
            UserId userId = UserId.generate();
            OpenIdCredential credential = OpenIdCredential.create(userId, OpenIdProvider.GOOGLE, "google-user-123456");
            assertThat(credential.getLastUsedAt()).isNull();

            credential.recordUsage();

            assertThat(credential.getLastUsedAt()).isNotNull();
        }
    }
}
