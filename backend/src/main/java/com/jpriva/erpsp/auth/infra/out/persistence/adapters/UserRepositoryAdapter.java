package com.jpriva.erpsp.auth.infra.out.persistence.adapters;

import com.jpriva.erpsp.auth.domain.model.user.User;
import com.jpriva.erpsp.shared.domain.model.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.UserRepositoryPort;
import com.jpriva.erpsp.auth.infra.out.persistence.entities.UserEntity;
import com.jpriva.erpsp.auth.infra.out.persistence.mapper.UserMapper;
import com.jpriva.erpsp.shared.domain.model.Email;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter that implements UserRepositoryPort using Spring Data JPA.
 * Converts between domain model (User) and JPA entity (UserEntity).
 */
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final UserJpaRepository jpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(User user) {
        UserEntity entity = UserMapper.domainToEntity(user);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return jpaRepository.findById(userId.value())
                .map(UserMapper::entityToDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value())
                .map(UserMapper::entityToDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }
}
