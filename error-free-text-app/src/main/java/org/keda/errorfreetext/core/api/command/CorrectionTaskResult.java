package org.keda.errorfreetext.core.api.command;

import org.keda.errorfreetext.core.domain.TaskStatus;

import java.util.UUID;

public record CorrectionTaskResult(
        UUID uuid,
        TaskStatus status,
        String correctedText,
        String errorMessage
) {

}
