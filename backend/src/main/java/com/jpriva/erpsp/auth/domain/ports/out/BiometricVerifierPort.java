package com.jpriva.erpsp.auth.domain.ports.out;

import com.jpriva.erpsp.auth.domain.model.credential.BiometricType;

/**
 * Port for verifying biometric samples against stored templates.
 */
public interface BiometricVerifierPort {

    /**
     * Verifies a biometric sample against a stored template.
     *
     * @param biometricSample the biometric sample data
     * @param templateId      the stored template identifier
     * @param biometricType   the type of biometric
     * @return true if the sample matches the template within acceptable threshold
     */
    boolean verify(byte[] biometricSample, String templateId, BiometricType biometricType);

    /**
     * Enrolls a new biometric template.
     *
     * @param biometricSample the biometric sample data
     * @param biometricType   the type of biometric
     * @return the template identifier for the enrolled biometric
     */
    String enroll(byte[] biometricSample, BiometricType biometricType);
}
