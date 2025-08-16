package com.cmms.lite.security.config;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
    import org.springframework.stereotype.Component;
    import org.springframework.validation.annotation.Validated;


    @Data
    @Validated
    @Component
    @ConfigurationProperties(prefix = "jwt.token")
    public class JwtProperties {

        @NotBlank(message = "JWT secret key cannot be blank")
    @Size(min = 32, message = "JWT secret key must have at least 32 characters for HS256 algorithm")
    private String secretKey;

    @Min(value = 60000, message = "Token duration must be at least 1 minute (60000ms)")
    private Long duration = 3600000L;

    @NotBlank(message = "JWT issuer cannot be blank")
    private String issuer = "gruchh";
}