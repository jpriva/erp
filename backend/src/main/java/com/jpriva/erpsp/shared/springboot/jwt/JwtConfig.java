package com.jpriva.erpsp.shared.springboot.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtConfig {

    /**
     * Secret key for signing tokens (min 256 bits / 32 chars for HS256)
     */
    private String secret;

    /**
     * Access token expiration in seconds (default: 15 minutes)
     */
    private long accessTokenExpiration = 900;

    /**
     * Refresh token expiration in seconds (default: 7 days)
     */
    private long refreshTokenExpiration = 604800;
}
