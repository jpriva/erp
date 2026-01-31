package com.jpriva.erpsp.auth.infra.out.persistence.adapters;

import com.jpriva.erpsp.auth.domain.model.credential.Credential;
import com.jpriva.erpsp.auth.domain.model.credential.CredentialId;
import com.jpriva.erpsp.auth.domain.model.credential.CredentialType;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.CredentialRepositoryPort;
import com.jpriva.erpsp.auth.infra.out.persistence.entities.CredentialEntity;
import com.jpriva.erpsp.auth.infra.out.persistence.mapper.CredentialMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter that implements CredentialRepositoryPort using Spring Data JPA.
 * Converts between domain models (PasswordCredential, OpenIdCredential) and JPA entities.
 * Handles the sealed class hierarchy through the mapper.
 */
@Component
public class CredentialRepositoryAdapter implements CredentialRepositoryPort {
    private final CredentialJpaRepository jpaRepository;

    public CredentialRepositoryAdapter(CredentialJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Credential credential) {
        CredentialEntity entity = CredentialMapper.domainToEntity(credential);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Credential> findById(CredentialId credentialId) {
        return jpaRepository.findById(credentialId.value())
                .map(CredentialMapper::entityToDomain);
    }

    @Override
    public List<Credential> findByUserId(UserId userId) {
        return jpaRepository.findByUserId(userId.value())
                .stream()
                .map(CredentialMapper::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Credential> findPasswordByUserId(UserId userId) {
        Optional<CredentialEntity> credential = Optional.of(jpaRepository.findByUserIdAndType(userId.value(), CredentialType.PASSWORD.toString())
                .getFirst());
        return credential.map(CredentialMapper::entityToDomain);
    }

    @Override
    public void deleteById(CredentialId credentialId) {
        jpaRepository.deleteById(credentialId.value());
    }

    @Override
    public void deleteByUserId(UserId userId) {
        List<CredentialEntity> credentials = jpaRepository.findByUserId(userId.value());
        jpaRepository.deleteAll(credentials);
    }

    @Override
    public boolean existsById(CredentialId credentialId) {
        return jpaRepository.existsById(credentialId.value());
    }
}
