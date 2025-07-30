package com.cmms.lite.security.dto;

public record UserSummaryDto(
        Long id,
        String username,
        String email
) {
}
