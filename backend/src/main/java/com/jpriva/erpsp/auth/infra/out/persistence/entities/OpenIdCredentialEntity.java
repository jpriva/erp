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
 * JPA entity for OpenID Connect / OAuth2 credentials.
 * Extends CredentialEntity with OpenID-specific columns.
 */
@Entity
@DiscriminatorValue("OPENID")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OpenIdCredentialEntity extends CredentialEntity {
    @Column(name = "provider", nullable = true, length = 50)
    private String provider;

    @Column(name = "subject", nullable = true, length = 255)
    private String subject;
}
