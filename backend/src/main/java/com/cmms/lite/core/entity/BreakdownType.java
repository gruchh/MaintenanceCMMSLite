package com.cmms.lite.core.entity;

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