package com.jpriva.erpsp.auth.domain.ports.out;

import com.jpriva.erpsp.auth.domain.model.credential.Credential;
import com.jpriva.erpsp.auth.domain.model.credential.CredentialId;
import com.jpriva.erpsp.shared.domain.model.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Port for credential persistence operations.
 */
public interface CredentialRepositoryPort {

    /**
     * Saves a credential.
     *
     * @param credential the credential to save
     */
    void save(Credential credential);

    /**
     * Finds a credential by its ID.
     *
     * @param credentialId the credential ID
     * @return the credential if found
     */
    Optional<Credential> findById(CredentialId credentialId);

    /**
     * Finds all credentials for a user.
     *
     * @param userId the user ID
     * @return list of credentials for the user
     */
    List<Credential> findByUserId(UserId userId);

    /**
     * Finds all credentials for a user of a PASSWORD type.
     *
     * @param userId the user ID
     * @return optional of credential for the user with type PASSWORD.
     */
    Optional<Credential> findPasswordByUserId(UserId userId);

    /**
     * Deletes a credential by its ID.
     *
     * @param credentialId the credential ID
     */
    void deleteById(CredentialId credentialId);

    /**
     * Deletes all credentials for a user.
     *
     * @param userId the user ID
     */
    void deleteByUserId(UserId userId);

    /**
     * Checks if a credential exists by ID.
     *
     * @param credentialId the credential ID
     * @return true if the credential exists
     */
    boolean existsById(CredentialId credentialId);
}
