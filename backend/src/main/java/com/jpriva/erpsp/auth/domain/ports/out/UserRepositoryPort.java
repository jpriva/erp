package com.jpriva.erpsp.auth.domain.ports.out;

import com.jpriva.erpsp.auth.domain.model.user.User;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.shared.domain.model.Email;

import java.util.Optional;

/**
 * Port for user persistence operations.
 */
public interface UserRepositoryPort {

    /**
     * Saves a user.
     *
     * @param user the user to save
     */
    void save(User user);

    /**
     * Finds a user by its ID.
     *
     * @param userId the user ID
     * @return the user with the given ID, or empty if not found
     */
    Optional<User> findById(UserId userId);

    /**
     * Finds a user by their email address.
     *
     * @param email the email address
     * @return the user with the given email, or empty if not found
     */
    Optional<User> findByEmail(Email email);

    /**
     * Checks if a user with the given email exists.
     *
     * @param email the email address
     * @return true if a user with the given email exists, false otherwise
     */
    boolean existsByEmail(Email email);
}
