package com.jpriva.erpsp.auth.application.dto;

import com.jpriva.erpsp.auth.domain.model.user.User;

public record UserView(
        String id,
        String email,
        String name,
        String status
) {
    public static UserView from(User user) {
        return new UserView(
                user.getUserId().toString(),
                user.getEmail().value(),
                user.getName().fullName(),
                user.getStatus().name()
        );
    }
}
