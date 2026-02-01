package com.jpriva.erpsp.auth.infra.out.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * JPA entity for password-based credentials.
 * Extends CredentialEntity with password-specific columns.
 */
@Entity
@DiscriminatorValue("PASSWORD")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PasswordCredentialEntity extends CredentialEntity {
    @Column(name = "password_hash")
    private String passwordHash;
}
