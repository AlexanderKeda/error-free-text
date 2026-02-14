package org.keda.errorfreetext.core.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keda.errorfreetext.core.domain.CorrectionTaskEntity;
import org.keda.errorfreetext.core.domain.TaskStatus;
import org.keda.errorfreetext.core.repositories.CorrectionTaskRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Mock
    private CorrectionTaskRepository repository;

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

    @Test
    void shouldHandleTaskFailureAndResetStatusToNew() {
        CorrectionTaskEntity successfulTask = newTask(TaskStatus.PROCESSING);
        CorrectionTaskEntity failedTask = newTask(TaskStatus.PROCESSING);

        List<CorrectionTaskEntity> tasks = List.of(successfulTask, failedTask);

        RuntimeException ex = new RuntimeException("simulated failure");
        CompletableFuture<CorrectionTaskEntity> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(ex);

        when(taskProvider.getNewTasks()).thenReturn(tasks);
        when(taskProcessor.process(successfulTask))
                .thenReturn(CompletableFuture.completedFuture(successfulTask));
        when(taskProcessor.process(failedTask)).thenReturn(failedFuture);

        service.processNewTasks();

        verify(taskProvider, times(1)).getNewTasks();
        verify(taskProcessor, times(tasks.size())).process(any(CorrectionTaskEntity.class));
        verify(repository, times(1)).save(failedTask);

        assertEquals(TaskStatus.PROCESSING, successfulTask.getTaskStatus());
        assertEquals(TaskStatus.NEW, failedTask.getTaskStatus());
    }


    private CorrectionTaskEntity newTask() {
        return new CorrectionTaskEntity();
    }

    private CorrectionTaskEntity newTask(TaskStatus status) {
        var task = new CorrectionTaskEntity();
        task.setTaskStatus(status);
        return task;
    }

}