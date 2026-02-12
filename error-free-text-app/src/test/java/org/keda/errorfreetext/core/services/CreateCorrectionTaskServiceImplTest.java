package org.keda.errorfreetext.core.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keda.errorfreetext.core.api.command.CreateCorrectionTaskCommand;
import org.keda.errorfreetext.core.domain.CorrectionTaskEntity;
import org.keda.errorfreetext.core.domain.Language;
import org.keda.errorfreetext.core.domain.TaskStatus;
import org.keda.errorfreetext.core.repositories.CorrectionTaskRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCorrectionTaskServiceImplTest {

    @Mock
    private CorrectionTaskRepository repository;

    @InjectMocks
    private CreateCorrectionTaskServiceImpl service;

    @Test
    void shouldReturnCorrectResult() {
        var command = new CreateCorrectionTaskCommand("text", Language.EN);
        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        var result = service.create(command);
        assertNotNull(result);
        assertNotNull(result.uuid());
        verify(repository, times(1)).save(any());
    }

    @Test
    void shouldBuildAndSaveEntityWithCorrectFields() {
        var command = new CreateCorrectionTaskCommand("text", Language.EN);
        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<CorrectionTaskEntity> captor =
                ArgumentCaptor.forClass(CorrectionTaskEntity.class);

        service.create(command);
        verify(repository).save(captor.capture());
        var createdEntity = captor.getValue();

        assertNotNull(createdEntity);
        assertNotNull(createdEntity.getTaskUuid());
        assertEquals(command.text(), createdEntity.getOriginalText());
        assertEquals(command.language(), createdEntity.getLanguage());
        assertEquals(TaskStatus.NEW, createdEntity.getTaskStatus());
    }

}