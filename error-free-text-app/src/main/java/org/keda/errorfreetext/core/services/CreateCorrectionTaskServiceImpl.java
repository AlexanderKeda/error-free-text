package org.keda.errorfreetext.core.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keda.errorfreetext.core.api.command.CreateCorrectionTaskCommand;
import org.keda.errorfreetext.core.api.command.CreateCorrectionTaskResult;
import org.keda.errorfreetext.core.domain.CorrectionTaskEntity;
import org.keda.errorfreetext.core.domain.TaskStatus;
import org.keda.errorfreetext.core.repositories.CorrectionTaskRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class CreateCorrectionTaskServiceImpl implements CreateCorrectionTaskService {

    private final CorrectionTaskRepository correctionTaskRepository;

    @Override
    public CreateCorrectionTaskResult create(CreateCorrectionTaskCommand command) {
        var savedCorrectionTaskEntity = correctionTaskRepository.save(buildCorrectionTaskEntity(command));
        log.info("Created task {}", savedCorrectionTaskEntity.getTaskUuid());
        return new CreateCorrectionTaskResult(savedCorrectionTaskEntity.getTaskUuid());
    }

    private CorrectionTaskEntity buildCorrectionTaskEntity(CreateCorrectionTaskCommand command) {
        return CorrectionTaskEntity
                .builder()
                .taskUuid(UUID.randomUUID())
                .originalText(command.text())
                .language(command.language())
                .taskStatus(TaskStatus.NEW)
                .build();
    }
}
