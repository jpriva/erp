package com.jpriva.erpsp.auth.domain.ports.out;

public interface PasswordHasherPort {
    String encode(String raw);
    boolean matches(String raw, String encoded);
}
