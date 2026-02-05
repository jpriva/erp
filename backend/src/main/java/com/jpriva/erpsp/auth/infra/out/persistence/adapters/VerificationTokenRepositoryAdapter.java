package com.jpriva.erpsp.auth.infra.out.persistence.adapters;

import com.jpriva.erpsp.shared.domain.model.token.verification.VerificationToken;
import com.jpriva.erpsp.auth.domain.ports.out.VerificationTokenRepositoryPort;
import com.jpriva.erpsp.auth.infra.out.persistence.mapper.VerificationTokenMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class VerificationTokenRepositoryAdapter implements VerificationTokenRepositoryPort {

    private final VerificationTokenJPARepository jpaRepository;

    public VerificationTokenRepositoryAdapter(VerificationTokenJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(VerificationToken token) {
        jpaRepository.save(VerificationTokenMapper.domainToEntity(token));
    }

    @Override
    public Optional<VerificationToken> findByToken(UUID token) {
        return jpaRepository.findById(token)
                .map(VerificationTokenMapper::entityToDomain);
    }

    @Override
    public void deleteByToken(UUID token) {
        jpaRepository.deleteById(token);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        jpaRepository.deleteByUserId(userId);
    }
}
