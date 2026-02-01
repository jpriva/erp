package com.jpriva.erpsp.auth.application.dto;

/**
 * Command to exchange a base token for one with tenant/role context.
 */
public record ExchangeTokenCommand(
        String accessToken,
        String tenantId,
        String roleName
) {
}
