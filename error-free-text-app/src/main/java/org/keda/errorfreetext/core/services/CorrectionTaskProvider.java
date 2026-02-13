package org.keda.errorfreetext.core.services;

import lombok.extern.slf4j.Slf4j;
import org.keda.errorfreetext.core.domain.CorrectionTaskEntity;
import org.keda.errorfreetext.core.domain.TaskStatus;
import org.keda.errorfreetext.core.repositories.CorrectionTaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
class CorrectionTaskProvider {

    private final CorrectionTaskRepository repository;
    private final int pageSize;

    CorrectionTaskProvider(
            CorrectionTaskRepository repository,
            @Value("${correction.task.provider.page-size}") int pageSize
    ) {
        this.repository = repository;
        this.pageSize = pageSize;
    }

    @Transactional
    public List<CorrectionTaskEntity> getNewTasks() {
        List<CorrectionTaskEntity> tasks = repository
                .findByTaskStatusOrderByCreatedAtAsc(TaskStatus.NEW, Pageable.ofSize(pageSize));
        tasks.forEach(task -> task.setTaskStatus(TaskStatus.PROCESSING));
        log.info("Picked up {} new tasks for processing", tasks.size());
        return tasks;
    }
}
