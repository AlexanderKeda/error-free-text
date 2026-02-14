package org.keda.errorfreetext.core.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keda.errorfreetext.core.domain.CorrectionTaskEntity;
import org.keda.errorfreetext.core.domain.TaskStatus;
import org.keda.errorfreetext.core.repositories.CorrectionTaskRepository;
import org.keda.errorfreetext.properties.CorrectionTaskProviderProperties;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorrectionTaskProviderTest {

    @Mock
    private CorrectionTaskRepository repository;

    @Mock
    private CorrectionTaskProviderProperties properties;

    @InjectMocks
    private CorrectionTaskProvider provider;

    private static final int PAGE_SIZE = 10;

    @Test
    void shouldChangeStatusOfReturnedEntitiesToProcessing() {
        CorrectionTaskEntity task1 = newTask();
        CorrectionTaskEntity task2 = newTask();
        List<CorrectionTaskEntity> tasks = List.of(task1, task2);
        when(properties.pageSize()).thenReturn(PAGE_SIZE);
        when(repository.findByTaskStatusOrderByCreatedAtAsc(
                TaskStatus.NEW, Pageable.ofSize(PAGE_SIZE))
        ).thenReturn(tasks);
        List<CorrectionTaskEntity> result = provider.getNewTasks();

        assertNotNull(result);
        assertEquals(tasks.size(), result.size());
        for(CorrectionTaskEntity task: result) {
            assertEquals(TaskStatus.PROCESSING, task.getTaskStatus());
        }
        assertEquals(TaskStatus.PROCESSING, result.get(0).getTaskStatus());
        assertEquals(TaskStatus.PROCESSING, result.get(1).getTaskStatus());
    }

    private CorrectionTaskEntity newTask() {
        CorrectionTaskEntity task = new CorrectionTaskEntity();
        task.setTaskStatus(TaskStatus.NEW);
        return task;
    }

}