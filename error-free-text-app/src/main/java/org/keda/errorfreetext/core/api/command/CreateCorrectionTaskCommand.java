package org.keda.errorfreetext.core.api.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.keda.errorfreetext.core.domain.Language;
import org.keda.errorfreetext.core.validation.annotation.ValidText;

public record CreateCorrectionTaskCommand(
        @NotBlank(message = "не может быть пустым!") @ValidText String text,
        @NotNull(message = "не указан!") Language language
) {
}
