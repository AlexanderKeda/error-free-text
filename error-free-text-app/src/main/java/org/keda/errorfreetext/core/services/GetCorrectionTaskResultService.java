package org.keda.errorfreetext.core.services;

import org.keda.errorfreetext.core.api.command.CorrectionTaskResult;
import org.keda.errorfreetext.core.api.command.CorrectionTaskResultQuery;

public interface GetCorrectionTaskResultService {

    CorrectionTaskResult get(CorrectionTaskResultQuery query);

}
