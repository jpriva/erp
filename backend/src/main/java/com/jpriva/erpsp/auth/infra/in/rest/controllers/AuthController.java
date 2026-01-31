package com.jpriva.erpsp.auth.infra.in.rest.controllers;

import com.jpriva.erpsp.auth.application.dto.UserView;
import com.jpriva.erpsp.auth.application.usecases.LoginLocalUserUseCase;
import com.jpriva.erpsp.auth.application.usecases.RegisterLocalUserUseCase;
import com.jpriva.erpsp.auth.infra.in.rest.dto.LocalLoginRequestDto;
import com.jpriva.erpsp.auth.infra.in.rest.dto.RegisterLocalUserRequestDto;
import com.jpriva.erpsp.auth.infra.in.rest.dto.TokenResponseDto;
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
    private final LoginLocalUserUseCase loginLocalUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerWithPassword(@RequestBody RegisterLocalUserRequestDto request) {
        UserView userResponse = registerLocalUserUseCase.execute(request.toCommand(), request.password());
        return ResponseEntity.ok(UserResponseDto.from(userResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LocalLoginRequestDto request) {
        TokenResponseDto tokenResponseDto =
                TokenResponseDto.from(loginLocalUserUseCase.execute(request.toCommand()));
        return ResponseEntity.ok(tokenResponseDto);
    }
}
