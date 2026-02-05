package com.jpriva.erpsp.auth.infra.out.persistence.mapper;

import com.jpriva.erpsp.shared.domain.model.token.verification.VerificationToken;
import com.jpriva.erpsp.auth.infra.out.persistence.entities.VerificationTokenEntity;

/**
 * Mapper for converting between VerificationToken domain model and VerificationTokenEntity JPA entity.
 */
public class VerificationTokenMapper {
    private VerificationTokenMapper() {
    }

    /**
     * Converts a VerificationToken domain model to a VerificationTokenEntity JPA entity.
     *
     * @param user the VerificationToken domain model
     * @return the VerificationTokenEntity JPA entity
     */
    public static VerificationTokenEntity domainToEntity(VerificationToken user) {
        return VerificationTokenEntity.builder()
                .verificationTokenId(user.getId())
                .userId(user.getUserId().value())
                .expiryDate(user.getExpiryDate())
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * Converts a VerificationTokenEntity JPA entity to a VerificationToken domain model.
     *
     * @param entity the VerificationTokenEntity JPA entity
     * @return the VerificationToken domain model
     */
    public static VerificationToken entityToDomain(VerificationTokenEntity entity) {
        return VerificationToken.fromDatabase(
                entity.getVerificationTokenId(),
                entity.getUserId(),
                entity.getExpiryDate(),
                entity.getCreatedAt()
        );
    }
}
