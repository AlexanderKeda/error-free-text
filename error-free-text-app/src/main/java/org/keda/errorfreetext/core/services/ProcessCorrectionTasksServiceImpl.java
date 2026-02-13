package org.keda.errorfreetext.core.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keda.errorfreetext.core.domain.CorrectionTaskEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class ProcessCorrectionTasksServiceImpl implements ProcessCorrectionTasksService {

    private final CorrectionTaskProvider taskProvider;
    private final SingleCorrectionTaskProcessor taskProcessor;

    @Override
    public void processNewTasks() {
        List<CorrectionTaskEntity> tasks = taskProvider.getNewTasks();
        List<CompletableFuture<CorrectionTaskEntity>> futures = tasks.stream()
                .map(taskProcessor::process)
                .toList();
        List<CorrectionTaskEntity> savedTasks = futures.stream()
                .map(CompletableFuture::join)
                        .toList();
        log.info("Finished processing of {} new correction tasks", savedTasks.size());
    }
}
