package com.jpriva.erpsp.auth.infra.in.rest.dto;

import com.jpriva.erpsp.auth.application.dto.RegisterUserCommand;

public record RegisterLocalUserRequestDto(
        String email,
        String firstName,
        String lastName,
        String password
) {
    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(email, firstName, lastName);
    }
}
