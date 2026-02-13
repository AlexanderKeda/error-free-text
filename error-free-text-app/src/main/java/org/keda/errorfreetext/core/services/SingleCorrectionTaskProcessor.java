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
        String correctedText;
        try {
            correctedText = textCorrectionProcessor
                    .correct(taskEntity.getOriginalText(), taskEntity.getLanguage());
        } catch (Exception e) {
            log.warn(
                    "Failed to process correction task [uuid={}, language={}, text='{}']: {}",
                    taskEntity.getTaskUuid(),
                    taskEntity.getLanguage(),
                    taskEntity.getOriginalText(),
                    e.getMessage(),
                    e
            );
            taskEntity.setErrorMessage(e.getMessage());
            taskEntity.setTaskStatus(TaskStatus.ERROR);
            return repository.save(taskEntity);
        }
        taskEntity.setCorrectedText(correctedText);
        taskEntity.setTaskStatus(TaskStatus.DONE);
        return repository.save(taskEntity);
    }
}
