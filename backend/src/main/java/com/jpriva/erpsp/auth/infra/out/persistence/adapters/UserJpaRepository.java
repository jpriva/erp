package com.jpriva.erpsp.auth.infra.out.persistence.adapters;

import com.jpriva.erpsp.auth.infra.out.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for UserEntity.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    /**
     * Finds a user by their email address.
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Checks if a user exists by email.
     */
    boolean existsByEmail(String email);
}
