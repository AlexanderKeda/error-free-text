package org.keda.errorfreetext.core.services;

import org.keda.errorfreetext.core.api.command.CreateCorrectionTaskCommand;
import org.keda.errorfreetext.core.api.command.CreateCorrectionTaskResult;

public interface CreateCorrectionTaskService {

    CreateCorrectionTaskResult create(CreateCorrectionTaskCommand command);

}
