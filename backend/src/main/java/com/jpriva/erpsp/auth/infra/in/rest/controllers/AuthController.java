package com.jpriva.erpsp.auth.infra.in.rest.controllers;

import com.jpriva.erpsp.auth.application.dto.UserView;
import com.jpriva.erpsp.auth.application.usecases.LoginLocalUserUseCase;
import com.jpriva.erpsp.auth.application.usecases.RegisterLocalUserUseCase;
import com.jpriva.erpsp.auth.application.usecases.SendEmailVerificationUseCase;
import com.jpriva.erpsp.auth.application.usecases.VerifyEmailUseCase;
import com.jpriva.erpsp.auth.infra.in.rest.dto.LocalLoginRequestDto;
import com.jpriva.erpsp.auth.infra.in.rest.dto.RegisterLocalUserRequestDto;
import com.jpriva.erpsp.auth.infra.in.rest.dto.TokenResponseDto;
import com.jpriva.erpsp.auth.infra.in.rest.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterLocalUserUseCase registerLocalUserUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;
    private final LoginLocalUserUseCase loginLocalUserUseCase;
    private final SendEmailVerificationUseCase sendEmailVerificationUseCase;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerWithPassword(@RequestBody RegisterLocalUserRequestDto request) {
        UserView userResponse = registerLocalUserUseCase.execute(request.toCommand(), request.password(), null);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDto.from(userResponse));
    }

    @PostMapping("/resend-email-verification")
    public ResponseEntity<Void> resendEmailVerification(@RequestParam String email) {
        sendEmailVerificationUseCase.execute(email, null);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/verify-email/{token}")
    public ResponseEntity<Void> verifyEmail(@PathVariable String token) {
        verifyEmailUseCase.execute(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LocalLoginRequestDto request) {
        TokenResponseDto tokenResponseDto =
                TokenResponseDto.from(loginLocalUserUseCase.execute(request.toCommand()));
        return ResponseEntity.ok(tokenResponseDto);
    }
}
