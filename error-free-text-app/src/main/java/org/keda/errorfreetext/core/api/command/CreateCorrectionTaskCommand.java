package org.keda.errorfreetext.core.api.command;

import org.keda.errorfreetext.core.domain.Language;

public record CreateCorrectionTaskCommand(
        String text,
        Language language
) {
}
