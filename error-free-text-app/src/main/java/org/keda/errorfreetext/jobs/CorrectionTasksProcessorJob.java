package org.keda.errorfreetext.jobs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keda.errorfreetext.core.services.ProcessCorrectionTasksService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class CorrectionTasksProcessorJob {

    private final ProcessCorrectionTasksService processCorrectionTasksService;

    @Scheduled(fixedRate = 30000)
    void doJob() {
        log.info("Job: starting processing of new correction tasks");
        processCorrectionTasksService.processNewTasks();
        log.info("Job: finished work");
    }

}
