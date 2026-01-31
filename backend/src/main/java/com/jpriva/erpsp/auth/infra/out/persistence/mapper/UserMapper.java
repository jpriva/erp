package com.jpriva.erpsp.auth.infra.out.persistence.mapper;

import com.jpriva.erpsp.auth.domain.model.user.User;
import com.jpriva.erpsp.auth.infra.out.persistence.entities.UserEntity;

/**
 * Mapper for converting between User domain model and UserEntity JPA entity.
 */
public class UserMapper {
    private UserMapper() {
    }

    /**
     * Converts a User domain model to a UserEntity JPA entity.
     *
     * @param user the User domain model
     * @return the UserEntity JPA entity
     */
    public static UserEntity domainToEntity(User user) {
        return UserEntity.builder()
                .userId(user.getUserId().value())
                .email(user.getEmail().value())
                .firstName(user.getName().firstName())
                .lastName(user.getName().lastName())
                .status(user.getStatus().toString())
                .build();
    }

    /**
     * Converts a UserEntity JPA entity to a User domain model.
     *
     * @param entity the UserEntity JPA entity
     * @return the User domain model
     */
    public static User entityToDomain(UserEntity entity) {
        return User.fromPersistence(
                entity.getUserId(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
