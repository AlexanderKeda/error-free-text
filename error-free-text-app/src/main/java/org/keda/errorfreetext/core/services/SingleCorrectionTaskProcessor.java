package org.keda.errorfreetext.core.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keda.errorfreetext.core.domain.CorrectionTaskEntity;
import org.keda.errorfreetext.core.domain.TaskStatus;
import org.keda.errorfreetext.core.repositories.CorrectionTaskRepository;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class SingleCorrectionTaskProcessor {

    private final CorrectionTaskRepository repository;
    private final TextCorrectionProcessor textCorrectionProcessor;

    CorrectionTaskEntity process(CorrectionTaskEntity taskEntity) {
        log.info("Start of processing task: {}", taskEntity.getTaskUuid());
        String correctedText;
        try {
            correctedText = textCorrectionProcessor
                    .correct(taskEntity.getOriginalText(), taskEntity.getLanguage());
        } catch (Exception e) {
            log.warn(
                    "Failed to process task uuid={}. {}: {}",
                    taskEntity.getTaskUuid(),
                    e.getClass(),
                    e.getMessage()
            );
            taskEntity.setErrorMessage(e.getMessage());
            taskEntity.setTaskStatus(TaskStatus.ERROR);
            return repository.save(taskEntity);
        }
        taskEntity.setCorrectedText(correctedText);
        taskEntity.setTaskStatus(TaskStatus.DONE);
        log.info("Task processing completed successfully, uuid={}", taskEntity.getTaskUuid());
        return repository.save(taskEntity);
    }
}
