package org.keda.errorfreetext.core.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keda.errorfreetext.core.domain.CorrectionTaskEntity;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessCorrectionTasksServiceImplTest {

    @Mock
    private CorrectionTaskProvider taskProvider;

    @Mock
    private SingleCorrectionTaskProcessor taskProcessor;

    @InjectMocks
    private ProcessCorrectionTasksServiceImpl service;

    @Test
    void shouldProcessSameNumberOfTasksAsProvided() {
        List<CorrectionTaskEntity> tasks = List.of(
                newTask(),
                newTask(),
                newTask()
        );

        when(taskProvider.getNewTasks())
                .thenReturn(tasks);
        when(taskProcessor.process(any()))
                .thenAnswer(invocation -> CompletableFuture.completedFuture(invocation.getArgument(0)));

        service.processNewTasks();

        verify(taskProvider, times(1)).getNewTasks();
        verify(taskProcessor, times(tasks.size())).process(any(CorrectionTaskEntity.class));
    }

    @Test
    void shouldNotProcessAnythingWhenNoNewTasksFound() {
        List<CorrectionTaskEntity> tasks = List.of();

        when(taskProvider.getNewTasks())
                .thenReturn(tasks);

        service.processNewTasks();

        verify(taskProvider, times(1)).getNewTasks();
        verifyNoInteractions(taskProcessor);
    }


    private CorrectionTaskEntity newTask() {
        return new CorrectionTaskEntity();
    }

}