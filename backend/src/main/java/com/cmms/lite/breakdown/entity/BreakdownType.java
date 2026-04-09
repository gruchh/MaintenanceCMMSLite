package com.cmms.lite.breakdown.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BreakdownType {

    MECHANICAL("Mechaniczna"),
    AUTOMATICAL("Automatyczna"),
    PARAMETERS("Parametryczna");

    private final String displayName;
}