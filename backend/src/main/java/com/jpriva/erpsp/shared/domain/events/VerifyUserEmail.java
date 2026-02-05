package com.jpriva.erpsp.shared.domain.events;

import java.time.Instant;
import java.util.UUID;

public record VerifyUserEmail(
        String language,
        UUID userId,
        String email,
        String name,
        Instant issuedAt,
        String token
) {
}
