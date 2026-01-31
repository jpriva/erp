package com.jpriva.erpsp.auth.application.dto;

import com.jpriva.erpsp.auth.domain.model.user.User;

public record UserResponse(
        String id,
        String email,
        String name,
        String status
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getUserId().toString(),
                user.getEmail().value(),
                user.getName().fullName(),
                user.getStatus().name()
        );
    }
}
