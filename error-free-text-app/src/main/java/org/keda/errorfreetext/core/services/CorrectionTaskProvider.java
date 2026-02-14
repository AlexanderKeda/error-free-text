package org.keda.errorfreetext.core.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keda.errorfreetext.core.domain.CorrectionTaskEntity;
import org.keda.errorfreetext.core.domain.TaskStatus;
import org.keda.errorfreetext.core.repositories.CorrectionTaskRepository;
import org.keda.errorfreetext.properties.CorrectionTaskProviderProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class CorrectionTaskProvider {

    private final CorrectionTaskRepository repository;
    private final CorrectionTaskProviderProperties properties;

    @Transactional
    public List<CorrectionTaskEntity> getNewTasks() {
        List<CorrectionTaskEntity> tasks = repository
                .findByTaskStatusOrderByCreatedAtAsc(TaskStatus.NEW, Pageable.ofSize(properties.pageSize()));
        tasks.forEach(task -> task.setTaskStatus(TaskStatus.PROCESSING));
        log.info("Picked up {} new tasks for processing", tasks.size());
        return tasks;
    }
}
