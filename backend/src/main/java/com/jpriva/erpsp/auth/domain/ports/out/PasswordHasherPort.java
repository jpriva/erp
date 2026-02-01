package com.jpriva.erpsp.auth.domain.ports.out;

/**
 * Port for password hashing operations.
 */
public interface PasswordHasherPort {

    /**
     * Hashes a raw password.
     *
     * @param raw Raw password to hash
     * @return Hashed password
     */
    String encode(String raw);

    /**
     * Compares a raw password with a hashed password.
     *
     * @param raw     Raw password to compare
     * @param encoded Hashed password to compare
     * @return True if the passwords match, false otherwise
     */
    boolean matches(String raw, String encoded);
}
