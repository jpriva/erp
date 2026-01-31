package com.jpriva.erpsp.auth.domain.model.utils;

import com.jpriva.erpsp.auth.domain.ports.out.PasswordHasherPort;

public class FakePasswordHasher implements PasswordHasherPort {
    @Override
    public String encode(String raw) {
        return "ENCODED_" + raw;
    }

    @Override
    public boolean matches(String raw, String encoded) {
        return encoded.endsWith(raw);
    }
}
