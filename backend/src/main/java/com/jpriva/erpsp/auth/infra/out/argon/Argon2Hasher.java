package com.jpriva.erpsp.auth.infra.out.argon;

import com.jpriva.erpsp.auth.domain.ports.out.PasswordHasherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class Argon2Hasher implements PasswordHasherPort {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(String raw) {
        return passwordEncoder.encode(raw);
    }

    @Override
    public boolean matches(String raw, String encoded) {
        return passwordEncoder.matches(raw, encoded);
    }
}
