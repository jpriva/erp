package com.jpriva.erpsp.auth.infra.in.rest.dto;

import com.jpriva.erpsp.auth.application.dto.LocalLoginCommand;

public record LocalLoginRequestDto(
        String email,
        String password
) {
    public LocalLoginCommand toCommand() {
        return new LocalLoginCommand(email, password);
    }

}
