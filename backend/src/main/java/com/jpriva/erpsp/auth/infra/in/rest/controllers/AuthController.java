package com.jpriva.erpsp.auth.infra.in.rest.controllers;

import com.jpriva.erpsp.auth.application.dto.UserResponse;
import com.jpriva.erpsp.auth.application.usecases.RegisterLocalUserUseCase;
import com.jpriva.erpsp.auth.infra.in.rest.dto.RegisterLocalUserRequest;
import com.jpriva.erpsp.auth.infra.in.rest.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterLocalUserUseCase registerLocalUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerWithPassword(@RequestBody RegisterLocalUserRequest request) {
        UserResponse userResponse = registerLocalUserUseCase.execute(request.toCommand(), request.password());
        return ResponseEntity.ok(UserResponseDto.from(userResponse));
    }
}
