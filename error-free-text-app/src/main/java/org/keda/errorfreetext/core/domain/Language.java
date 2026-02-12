package org.keda.errorfreetext.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Language {
    RU,
    EN;

    @JsonCreator
    public static Language from(String value) {
        return switch (value.trim().toUpperCase()) {
            case "EN" -> EN;
            case "RU" -> RU;
            default -> throw new IllegalArgumentException("Unsupported language: " + value);
        };
    }
}
