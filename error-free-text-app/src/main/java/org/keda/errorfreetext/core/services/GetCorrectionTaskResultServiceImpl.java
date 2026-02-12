package org.keda.errorfreetext.core.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.keda.errorfreetext.core.api.command.CorrectionTaskResult;
import org.keda.errorfreetext.core.api.command.CorrectionTaskResultQuery;
import org.keda.errorfreetext.core.domain.CorrectionTaskEntity;
import org.keda.errorfreetext.core.domain.TaskStatus;
import org.keda.errorfreetext.core.repositories.CorrectionTaskRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class GetCorrectionTaskResultServiceImpl implements GetCorrectionTaskResultService {

    private final CorrectionTaskRepository correctionTaskRepository;

    @Override
    public CorrectionTaskResult get(CorrectionTaskResultQuery query) {
        var correctionTaskOpt = correctionTaskRepository.findByTaskUuid(query.uuid());
        return correctionTaskOpt
                .map(this::buildCorrectionTaskResult)
                .orElseThrow(() -> new NoSuchElementException(
                        "Task with uuid: " + query.uuid() + " not found"
                ));
    }

    private CorrectionTaskResult buildCorrectionTaskResult(CorrectionTaskEntity correctionTask) {
        TaskStatus status = correctionTask.getTaskStatus();
        return switch (status) {
            case TaskStatus.NEW, TaskStatus.PROCESSING -> new CorrectionTaskResult(
                    correctionTask.getTaskUuid(),
                    status,
                    null,
                    null
            );
            case TaskStatus.ERROR -> new CorrectionTaskResult(
                    correctionTask.getTaskUuid(),
                    status,
                    null,
                    correctionTask.getErrorMessage()
            );
            case DONE -> new CorrectionTaskResult(
                    correctionTask.getTaskUuid(),
                    status,
                    correctionTask.getCorrectedText(),
                    null
            );
        };
    }
}
