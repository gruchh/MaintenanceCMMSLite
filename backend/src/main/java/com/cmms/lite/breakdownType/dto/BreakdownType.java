package com.cmms.lite.breakdownType;

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