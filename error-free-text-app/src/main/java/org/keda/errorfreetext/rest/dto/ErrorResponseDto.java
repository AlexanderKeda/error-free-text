package org.keda.errorfreetext.rest.dto;

import java.time.Instant;

public record ErrorResponseDto(
        String errorMessage,
        int errorCode,
        Instant timestamp,
        String path
) {
}
