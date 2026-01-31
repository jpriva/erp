package com.jpriva.erpsp.auth.application.dto;

import com.jpriva.erpsp.auth.domain.model.user.User;

public record RegisterUserCommand(
        String email,
        String firstName,
        String lastName
) {
    public User createUser() {
        return User.create(email, firstName, lastName);
    }
}
