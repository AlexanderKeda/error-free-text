package org.keda.errorfreetext.core.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keda.errorfreetext.core.api.command.CorrectionTaskResultQuery;
import org.keda.errorfreetext.core.domain.CorrectionTaskEntity;
import org.keda.errorfreetext.core.domain.TaskStatus;
import org.keda.errorfreetext.core.repositories.CorrectionTaskRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCorrectionTaskResultServiceImplTest {

    @Mock
    private CorrectionTaskRepository repository;

    @InjectMocks
    private GetCorrectionTaskResultServiceImpl service;

    @Test
    void shouldReturnResultForStatusNew() {
        var uuid = UUID.randomUUID();
        var correctText = "text";
        var errorMessage = "error";
        var query = new CorrectionTaskResultQuery(uuid);
        var taskEntity = CorrectionTaskEntity
                .builder()
                .taskUuid(uuid)
                .taskStatus(TaskStatus.NEW)
                .correctedText(correctText)
                .errorMessage(errorMessage)
                .build();
        when(repository.findByUuid(query.uuid()))
                .thenReturn(Optional.of(taskEntity));

        var result = service.get(query);
        assertNotNull(result);
        assertEquals(taskEntity.getTaskUuid(), result.uuid());
        assertEquals(TaskStatus.NEW, result.status());
        assertNull(result.correctedText());
        assertNull(result.errorMessage());
    }

    @Test
    void shouldReturnResultForStatusProcessing() {
        var uuid = UUID.randomUUID();
        var correctText = "text";
        var errorMessage = "error";
        var query = new CorrectionTaskResultQuery(uuid);
        var taskEntity = CorrectionTaskEntity
                .builder()
                .taskUuid(uuid)
                .taskStatus(TaskStatus.PROCESSING)
                .correctedText(correctText)
                .errorMessage(errorMessage)
                .build();
        when(repository.findByUuid(query.uuid()))
                .thenReturn(Optional.of(taskEntity));

        var result = service.get(query);
        assertNotNull(result);
        assertEquals(taskEntity.getTaskUuid(), result.uuid());
        assertEquals(TaskStatus.PROCESSING, result.status());
        assertNull(result.correctedText());
        assertNull(result.errorMessage());
    }

    @Test
    void shouldReturnResultForStatusError() {
        var uuid = UUID.randomUUID();
        var correctText = "text";
        var errorMessage = "error";
        var query = new CorrectionTaskResultQuery(uuid);
        var taskEntity = CorrectionTaskEntity
                .builder()
                .taskUuid(uuid)
                .taskStatus(TaskStatus.ERROR)
                .correctedText(correctText)
                .errorMessage(errorMessage)
                .build();
        when(repository.findByUuid(query.uuid()))
                .thenReturn(Optional.of(taskEntity));

        var result = service.get(query);
        assertNotNull(result);
        assertEquals(taskEntity.getTaskUuid(), result.uuid());
        assertEquals(TaskStatus.ERROR, result.status());
        assertNull(result.correctedText());
        assertEquals(errorMessage, result.errorMessage());
    }

    @Test
    void shouldReturnResultForStatusDone() {
        var uuid = UUID.randomUUID();
        var correctText = "text";
        var errorMessage = "error";
        var query = new CorrectionTaskResultQuery(uuid);
        var taskEntity = CorrectionTaskEntity
                .builder()
                .taskUuid(uuid)
                .taskStatus(TaskStatus.DONE)
                .correctedText(correctText)
                .errorMessage(errorMessage)
                .build();
        when(repository.findByUuid(query.uuid()))
                .thenReturn(Optional.of(taskEntity));

        var result = service.get(query);
        assertNotNull(result);
        assertEquals(taskEntity.getTaskUuid(), result.uuid());
        assertEquals(TaskStatus.DONE, result.status());
        assertEquals(correctText, result.correctedText());
        assertNull(result.errorMessage());
    }

    @Test
    void shouldThrowNotFoundWhenTaskDoesNotExist() {
        var uuid = UUID.randomUUID();
        var query = new CorrectionTaskResultQuery(uuid);
        var message = "Task with uuid: " + query.uuid() + " not found";
        when(repository.findByUuid(query.uuid()))
                .thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.get(query)
        );
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals(message, ex.getReason());
    }

}