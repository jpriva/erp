package com.jpriva.erpsp.auth.infra.out.persistence.adapters;

import com.jpriva.erpsp.auth.infra.out.persistence.entities.CredentialEntity;
import com.jpriva.erpsp.auth.infra.out.persistence.entities.OpenIdCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for CredentialEntity (base class).
 * Supports querying for all credential types and type-specific queries.
 */
@Repository
public interface CredentialJpaRepository extends JpaRepository<CredentialEntity, UUID> {
    /**
     * Finds all credentials for a user.
     */
    List<CredentialEntity> findByUserId(UUID userId);

    /**
     * Finds a specific OpenID credential by provider and subject.
     * This uses a custom query to filter only OpenID credentials.
     */
    @Query("SELECT c FROM OpenIdCredentialEntity c WHERE c.provider = :provider AND c.subject = :subject")
    Optional<OpenIdCredentialEntity> findByProviderAndSubject(
            @Param("provider") String provider,
            @Param("subject") String subject
    );

    /**
     * Finds all credentials for a user of a specific type.
     */
    @Query("SELECT c FROM CredentialEntity c WHERE c.userId = :userId AND c.type = :type")
    List<CredentialEntity> findByUserIdAndType(
            @Param("userId") UUID userId,
            @Param("type") String type
    );

    /**
     * Checks if a credential exists by type (for querying specific subtypes).
     */
    @Query("SELECT COUNT(c) > 0 FROM CredentialEntity c WHERE c.credentialId = :credentialId AND c.type = :type")
    boolean existsByIdAndType(
            @Param("credentialId") UUID credentialId,
            @Param("type") String type
    );
}
