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

class BiometricCredentialTest {

    @Nested
    class CreateTests {
        @Test
        void create_Success() {
            UserId userId = UserId.generate();
            BiometricCredential credential = BiometricCredential.create(
                    userId,
                    BiometricType.FINGERPRINT,
                    "template-123",
                    "device-abc"
            );

            assertThat(credential).isNotNull();
            assertThat(credential.getCredentialId()).isNotNull();
            assertThat(credential.getUserId()).isEqualTo(userId);
            assertThat(credential.getType()).isEqualTo(CredentialType.BIOMETRIC);
            assertThat(credential.getStatus()).isEqualTo(CredentialStatus.ACTIVE);
            assertThat(credential.getBiometricType()).isEqualTo(BiometricType.FINGERPRINT);
            assertThat(credential.getTemplateId().value()).isEqualTo("template-123");
            assertThat(credential.getDeviceId().value()).isEqualTo("device-abc");
            assertThat(credential.getCreatedAt()).isNotNull();
            assertThat(credential.getLastUsedAt()).isNull();
            assertThat(credential.isActive()).isTrue();
        }

        @Test
        void create_FailIfUserIdNull() {
            assertThatThrownBy(() -> BiometricCredential.create(
                    null,
                    BiometricType.FINGERPRINT,
                    "template-123",
                    "device-abc"
            ))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getPlainErrors())
                                .containsKey("userId");
                    });
        }

        @Test
        void create_FailIfBiometricTypeNull() {
            UserId userId = UserId.generate();
            assertThatThrownBy(() -> BiometricCredential.create(
                    userId,
                    null,
                    "template-123",
                    "device-abc"
            ))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getPlainErrors())
                                .containsKey("biometricType");
                    });
        }

        @Test
        void create_FailIfTemplateIdNullOrBlank() {
            UserId userId = UserId.generate();
            assertThatThrownBy(() -> BiometricCredential.create(
                    userId,
                    BiometricType.FINGERPRINT,
                    null,
                    "device-abc"
            ))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getPlainErrors())
                                .containsKey("biometricTemplateId");
                    });
        }

        @Test
        void create_FailIfDeviceIdNullOrBlank() {
            UserId userId = UserId.generate();
            assertThatThrownBy(() -> BiometricCredential.create(
                    userId,
                    BiometricType.FINGERPRINT,
                    "template-123",
                    null
            ))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getPlainErrors())
                                .containsKey("deviceId");
                    });
        }

        @Test
        void create_FailIfMultipleFieldsInvalid() {
            assertThatThrownBy(() -> BiometricCredential.create(null, null, null, null))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getPlainErrors())
                                .containsKey("userId")
                                .containsKey("biometricType")
                                .containsKey("biometricTemplateId")
                                .containsKey("deviceId");
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

            BiometricCredential credential = BiometricCredential.fromPersistence(
                    credentialId,
                    userId,
                    "FINGERPRINT",
                    "template-123",
                    "device-abc",
                    "ACTIVE",
                    createdAt,
                    lastUsedAt
            );

            assertThat(credential).isNotNull();
            assertThat(credential.getCredentialId().value()).isEqualTo(credentialId);
            assertThat(credential.getUserId().value()).isEqualTo(userId);
            assertThat(credential.getBiometricType()).isEqualTo(BiometricType.FINGERPRINT);
            assertThat(credential.getTemplateId().value()).isEqualTo("template-123");
            assertThat(credential.getDeviceId().value()).isEqualTo("device-abc");
            assertThat(credential.getStatus()).isEqualTo(CredentialStatus.ACTIVE);
            assertThat(credential.getCreatedAt()).isEqualTo(createdAt);
            assertThat(credential.getLastUsedAt()).isEqualTo(lastUsedAt);
        }

        @Test
        void fromPersistence_ThrowsPersistenceCompromised_WhenInvalidData() {
            assertThatThrownBy(() -> BiometricCredential.fromPersistence(
                    null,
                    UUID.randomUUID(),
                    "FINGERPRINT",
                    "template-123",
                    "device-abc",
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
        void verify_ReturnsTrue_WhenBiometricMatchesAndDeviceValid() {
            UserId userId = UserId.generate();
            BiometricCredential credential = BiometricCredential.create(
                    userId,
                    BiometricType.FINGERPRINT,
                    "template-123",
                    "device-abc"
            );

            byte[] biometricSample = new byte[]{1, 2, 3, 4, 5};
            boolean result = credential.verify(biometricSample, "device-abc", PasswordTestUtils.fakeBiometricVerifier);

            assertThat(result).isTrue();
        }

        @Test
        void verify_ReturnsFalse_WhenBiometricDoesNotMatch() {
            UserId userId = UserId.generate();
            BiometricCredential credential = BiometricCredential.create(
                    userId,
                    BiometricType.FINGERPRINT,
                    "template-123",
                    "device-abc"
            );

            byte[] invalidSample = new byte[]{};
            boolean result = credential.verify(invalidSample, "device-abc", PasswordTestUtils.fakeBiometricVerifier);

            assertThat(result).isFalse();
        }

        @Test
        void verify_ReturnsFalse_WhenDeviceIdDoesNotMatch() {
            UserId userId = UserId.generate();
            BiometricCredential credential = BiometricCredential.create(
                    userId,
                    BiometricType.FINGERPRINT,
                    "template-123",
                    "device-abc"
            );

            byte[] biometricSample = new byte[]{1, 2, 3, 4, 5};
            boolean result = credential.verify(biometricSample, "different-device", PasswordTestUtils.fakeBiometricVerifier);

            assertThat(result).isFalse();
        }

        @Test
        void verify_ReturnsFalse_WhenNotActive() {
            UserId userId = UserId.generate();
            BiometricCredential credential = BiometricCredential.create(
                    userId,
                    BiometricType.FINGERPRINT,
                    "template-123",
                    "device-abc"
            );
            credential.disable();

            byte[] biometricSample = new byte[]{1, 2, 3, 4, 5};
            boolean result = credential.verify(biometricSample, "device-abc", PasswordTestUtils.fakeBiometricVerifier);

            assertThat(result).isFalse();
        }
    }

    @Nested
    class StatusTests {
        @Test
        void disable_SetsStatusToDisabled() {
            UserId userId = UserId.generate();
            BiometricCredential credential = BiometricCredential.create(
                    userId,
                    BiometricType.FINGERPRINT,
                    "template-123",
                    "device-abc"
            );

            credential.disable();

            assertThat(credential.getStatus()).isEqualTo(CredentialStatus.DISABLED);
            assertThat(credential.isActive()).isFalse();
        }

        @Test
        void activate_SetsStatusToActive() {
            UserId userId = UserId.generate();
            BiometricCredential credential = BiometricCredential.create(
                    userId,
                    BiometricType.FINGERPRINT,
                    "template-123",
                    "device-abc"
            );
            credential.disable();

            credential.activate();

            assertThat(credential.getStatus()).isEqualTo(CredentialStatus.ACTIVE);
            assertThat(credential.isActive()).isTrue();
        }

        @Test
        void markAsCompromised_SetsStatusToCompromised() {
            UserId userId = UserId.generate();
            BiometricCredential credential = BiometricCredential.create(
                    userId,
                    BiometricType.FINGERPRINT,
                    "template-123",
                    "device-abc"
            );

            credential.markAsCompromised();

            assertThat(credential.getStatus()).isEqualTo(CredentialStatus.COMPROMISED);
            assertThat(credential.isActive()).isFalse();
        }

        @Test
        void recordUsage_SetsLastUsedAt() {
            UserId userId = UserId.generate();
            BiometricCredential credential = BiometricCredential.create(
                    userId,
                    BiometricType.FINGERPRINT,
                    "template-123",
                    "device-abc"
            );
            assertThat(credential.getLastUsedAt()).isNull();

            credential.recordUsage();

            assertThat(credential.getLastUsedAt()).isNotNull();
        }
    }
}
