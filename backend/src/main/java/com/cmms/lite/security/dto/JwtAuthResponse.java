package com.cmms.lite.security.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtAuthResponse {

    private String accessToken;
    private String tokenType;

    @Builder
    public JwtAuthResponse(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }
}
