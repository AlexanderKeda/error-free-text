package org.keda.errorfreetext.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Language {
    RU,
    EN;

    @JsonCreator
    public static Language from(String value) {
        if(value == null) {
            throw new IllegalArgumentException("Language is null");
        }
        return switch (value.trim().toUpperCase()) {
            case "EN" -> EN;
            case "RU" -> RU;
            default -> throw new IllegalArgumentException("Unsupported language: " + value);
        };
    }
}
