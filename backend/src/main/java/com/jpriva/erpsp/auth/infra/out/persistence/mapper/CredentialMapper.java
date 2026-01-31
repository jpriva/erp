package com.jpriva.erpsp.auth.infra.out.persistence.mapper;

import com.jpriva.erpsp.auth.domain.model.credential.Credential;
import com.jpriva.erpsp.auth.domain.model.credential.OpenIdCredential;
import com.jpriva.erpsp.auth.domain.model.credential.PasswordCredential;
import com.jpriva.erpsp.auth.infra.out.persistence.entities.CredentialEntity;
import com.jpriva.erpsp.auth.infra.out.persistence.entities.OpenIdCredentialEntity;
import com.jpriva.erpsp.auth.infra.out.persistence.entities.PasswordCredentialEntity;

/**
 * Mapper for converting between Credential domain models and CredentialEntity JPA entities.
 * </br>
 * Handles the sealed class hierarchy:
 * - PasswordCredential <-> PasswordCredentialEntity
 * - OpenIdCredential <-> OpenIdCredentialEntity
 */
public class CredentialMapper {
    private CredentialMapper() {
    }

    /**
     * Converts a Credential domain model to the appropriate CredentialEntity JPA entity.
     * Uses pattern matching to detect the concrete type.
     *
     * @param credential the Credential domain model
     * @return the appropriate CredentialEntity JPA entity
     */
    public static CredentialEntity domainToEntity(Credential credential) {
        if (credential instanceof PasswordCredential passwordCred) {
            return domainToPasswordEntity(passwordCred);
        } else if (credential instanceof OpenIdCredential openIdCred) {
            return domainToOpenIdEntity(openIdCred);
        } else {
            throw new IllegalArgumentException("Unknown credential type: " + credential.getClass().getName());
        }
    }

    /**
     * Converts a PasswordCredential domain model to a PasswordCredentialEntity.
     *
     * @param credential the PasswordCredential domain model
     * @return the PasswordCredentialEntity
     */
    private static PasswordCredentialEntity domainToPasswordEntity(PasswordCredential credential) {
        return PasswordCredentialEntity.builder()
                .credentialId(credential.getCredentialId().value())
                .userId(credential.getUserId().value())
                .status(credential.getStatus().toString())
                .createdAt(credential.getCreatedAt())
                .lastUsedAt(credential.getLastUsedAt())
                .passwordHash(credential.getPassword().hash())
                .build();
    }

    /**
     * Converts an OpenIdCredential domain model to an OpenIdCredentialEntity.
     *
     * @param credential the OpenIdCredential domain model
     * @return the OpenIdCredentialEntity
     */
    private static OpenIdCredentialEntity domainToOpenIdEntity(OpenIdCredential credential) {
        return OpenIdCredentialEntity.builder()
                .credentialId(credential.getCredentialId().value())
                .userId(credential.getUserId().value())
                .status(credential.getStatus().toString())
                .createdAt(credential.getCreatedAt())
                .lastUsedAt(credential.getLastUsedAt())
                .provider(credential.getProvider().name())
                .subject(credential.getSubject().value())
                .build();
    }

    /**
     * Converts a CredentialEntity JPA entity to the appropriate Credential domain model.
     * Uses the discriminator column (type) to detect the concrete entity type.
     *
     * @param entity the CredentialEntity JPA entity
     * @return the appropriate Credential domain model
     */
    public static Credential entityToDomain(CredentialEntity entity) {
        if (entity instanceof PasswordCredentialEntity passwordEntity) {
            return entityToPasswordDomain(passwordEntity);
        } else if (entity instanceof OpenIdCredentialEntity openIdEntity) {
            return entityToOpenIdDomain(openIdEntity);
        } else {
            throw new IllegalArgumentException("Unknown credential entity type: " + entity.getClass().getName());
        }
    }

    /**
     * Converts a PasswordCredentialEntity to a PasswordCredential domain model.
     *
     * @param entity the PasswordCredentialEntity
     * @return the PasswordCredential domain model
     */
    private static PasswordCredential entityToPasswordDomain(PasswordCredentialEntity entity) {
        return PasswordCredential.fromPersistence(
                entity.getCredentialId(),
                entity.getUserId(),
                entity.getPasswordHash(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getLastUsedAt()
        );
    }

    /**
     * Converts an OpenIdCredentialEntity to an OpenIdCredential domain model.
     */
    private static OpenIdCredential entityToOpenIdDomain(OpenIdCredentialEntity entity) {
        return OpenIdCredential.fromPersistence(
                entity.getCredentialId(),
                entity.getUserId(),
                entity.getProvider(),
                entity.getSubject(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getLastUsedAt()
        );
    }
}
