package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.BiometricVerifierPort;
import com.jpriva.erpsp.shared.domain.exceptions.ErpPersistenceCompromisedException;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.time.Instant;
import java.util.UUID;

/**
 * Credential type for biometric authentication (fingerprint, face, iris, voice).
 */
public final class BiometricCredential extends Credential {
    private static final String FIELD_USER_ID = "userId";
    private static final String USER_ID_NULL_ERROR = "User ID can't be empty";
    private static final String FIELD_BIOMETRIC_TYPE = "biometricType";
    private static final String BIOMETRIC_TYPE_NULL_ERROR = "Biometric type can't be empty";
    private static final String FIELD_TEMPLATE_ID = "templateId";
    private static final String TEMPLATE_ID_NULL_ERROR = "Template ID can't be empty";
    private static final String FIELD_DEVICE_ID = "deviceId";
    private static final String DEVICE_ID_NULL_ERROR = "Device ID can't be empty";

    private final BiometricType biometricType;
    private final BiometricTemplateId templateId;
    private final DeviceId deviceId;

    private BiometricCredential(
            CredentialId credentialId,
            UserId userId,
            BiometricType biometricType,
            BiometricTemplateId templateId,
            DeviceId deviceId,
            CredentialStatus status,
            Instant createdAt,
            Instant lastUsedAt
    ) {
        super(credentialId, userId, CredentialType.BIOMETRIC, status, createdAt, lastUsedAt);
        this.biometricType = biometricType;
        this.templateId = templateId;
        this.deviceId = deviceId;
    }

    /**
     * Creates a new biometric credential for a user.
     *
     * @param userId        the user ID
     * @param biometricType the type of biometric
     * @param templateId    the biometric template identifier
     * @param deviceId      the device identifier
     * @return a new active biometric credential
     */
    public static BiometricCredential create(
            UserId userId,
            BiometricType biometricType,
            String templateId,
            String deviceId
    ) {
        var val = new ValidationError.Builder();
        if (userId == null) {
            val.addError(FIELD_USER_ID, USER_ID_NULL_ERROR);
        }
        if (biometricType == null) {
            val.addError(FIELD_BIOMETRIC_TYPE, BIOMETRIC_TYPE_NULL_ERROR);
        }

        BiometricTemplateId biometricTemplateId = null;
        try {
            biometricTemplateId = new BiometricTemplateId(templateId);
        } catch (ErpValidationException ex) {
            val.addValidation(ex.getValidationErrors());
        }

        DeviceId device = null;
        try {
            device = new DeviceId(deviceId);
        } catch (ErpValidationException ex) {
            val.addValidation(ex.getValidationErrors());
        }

        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        return new BiometricCredential(
                CredentialId.generate(),
                userId,
                biometricType,
                biometricTemplateId,
                device,
                CredentialStatus.ACTIVE,
                Instant.now(),
                null
        );
    }

    /**
     * Reconstructs a biometric credential from persistence.
     *
     * @param credentialId  the credential ID
     * @param userId        the user ID
     * @param biometricType the biometric type name
     * @param templateId    the template identifier
     * @param deviceId      the device identifier
     * @param status        the credential status
     * @param createdAt     the creation timestamp
     * @param lastUsedAt    the last usage timestamp
     * @return a reconstructed biometric credential
     */
    public static BiometricCredential fromPersistence(
            UUID credentialId,
            UUID userId,
            String biometricType,
            String templateId,
            String deviceId,
            String status,
            Instant createdAt,
            Instant lastUsedAt
    ) {
        try {
            return new BiometricCredential(
                    new CredentialId(credentialId),
                    new UserId(userId),
                    BiometricType.of(biometricType),
                    new BiometricTemplateId(templateId),
                    new DeviceId(deviceId),
                    CredentialStatus.of(status),
                    createdAt,
                    lastUsedAt
            );
        } catch (ErpValidationException ex) {
            throw new ErpPersistenceCompromisedException(AuthErrorCode.AUTH_MODULE, ex);
        }
    }

    /**
     * Verifies if the provided biometric sample matches this credential.
     *
     * @param biometricSample the biometric sample data (e.g., fingerprint scan)
     * @param requestDeviceId the device ID where the biometric was captured
     * @param verifier        the biometric verifier port
     * @return true if the biometric matches and device is valid, false otherwise
     */
    public boolean verify(byte[] biometricSample, String requestDeviceId, BiometricVerifierPort verifier) {
        if (!isActive()) {
            return false;
        }
        if (!deviceId.value().equals(requestDeviceId)) {
            return false;
        }
        return verifier.verify(biometricSample, templateId.value(), biometricType);
    }

    public BiometricType getBiometricType() {
        return biometricType;
    }

    public BiometricTemplateId getTemplateId() {
        return templateId;
    }

    public DeviceId getDeviceId() {
        return deviceId;
    }
}
