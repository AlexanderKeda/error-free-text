package org.keda.errorfreetext.core.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keda.errorfreetext.core.domain.CorrectionTaskEntity;
import org.keda.errorfreetext.core.domain.TaskStatus;
import org.keda.errorfreetext.core.repositories.CorrectionTaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class ProcessCorrectionTasksServiceImpl implements ProcessCorrectionTasksService {

    private final CorrectionTaskProvider taskProvider;
    private final SingleCorrectionTaskProcessor taskProcessor;
    private final CorrectionTaskRepository repository;

    @Override
    public void processNewTasks() {
        List<CorrectionTaskEntity> tasks = taskProvider.getNewTasks();
        List<CompletableFuture<CorrectionTaskEntity>> futures = tasks.stream()
                .map(task -> taskProcessor.process(task)
                        .exceptionally(ex -> handleFutureException(ex, task)))
                .toList();
        List<CorrectionTaskEntity> savedTasks = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .toList();
        log.info("Finished processing of {} new correction tasks", savedTasks.size());
    }

    private CorrectionTaskEntity handleFutureException(Throwable ex, CorrectionTaskEntity task) {
        task.setTaskStatus(TaskStatus.NEW);
        repository.save(task);
        log.error("Task {} failed. Status changed to NEW. {}: {}",
                task.getId(),
                ex.getClass().getSimpleName(),
                ex.getMessage());
        return null;
    }
}
