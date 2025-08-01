package com.cmms.lite.security.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN("ROLE_ADMIN"),
    TECHNICAN("ROLE_TECHNICAN"),
    SUBCONTRACTOR("ROLE_SUBCONTRACTOR");

    private final String authority;

    @Override
    public String toString() {
        return authority;
    }
}