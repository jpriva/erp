package com.jpriva.erpsp.auth.infra.in.rest.dto;

import com.jpriva.erpsp.auth.application.dto.UserView;

public record UserResponseDto(
        String id,
        String email,
        String name,
        String status
) {
    public static UserResponseDto from(UserView user) {
        return new UserResponseDto(
                user.id(),
                user.email(),
                user.name(),
                user.status()
        );
    }
}
