package org.keda.errorfreetext.core.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keda.errorfreetext.core.domain.CorrectionTaskEntity;
import org.keda.errorfreetext.core.domain.Language;
import org.keda.errorfreetext.core.domain.TaskStatus;
import org.keda.errorfreetext.core.repositories.CorrectionTaskRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SingleCorrectionTaskProcessorTest {

    @Mock
    private CorrectionTaskRepository repository;

    @Mock
    private TextCorrectionProcessor textCorrectionProcessor;

    @InjectMocks
    private SingleCorrectionTaskProcessor processor;

    private CorrectionTaskEntity taskEntity;

    @BeforeEach
    void setUp() {
        taskEntity = new CorrectionTaskEntity();
        taskEntity.setTaskUuid(UUID.randomUUID());
        taskEntity.setOriginalText("some text");
        taskEntity.setLanguage(Language.EN);
    }

    @Test
    void shouldSetErrorStatusAndMessageWhenTextCorrectionFails() {
        RuntimeException exception = new RuntimeException("processing failed");
        when(textCorrectionProcessor.correct("some text", Language.EN))
                .thenThrow(exception);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        CorrectionTaskEntity result = processor.process(taskEntity);

        assertEquals(TaskStatus.ERROR, result.getTaskStatus());
        assertEquals("processing failed", result.getErrorMessage());
        verify(repository, times(1)).save(taskEntity);
        verify(textCorrectionProcessor, times(1))
                .correct(taskEntity.getOriginalText(), taskEntity.getLanguage());
    }

    @Test
    void shouldSetDoneStatusAndSaveCorrectedTextWhenTextCorrectionSucceeds() {
        when(textCorrectionProcessor.correct("some text", Language.EN))
                .thenReturn("corrected text");
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        CorrectionTaskEntity result = processor.process(taskEntity);

        assertEquals(TaskStatus.DONE, result.getTaskStatus());
        assertEquals("corrected text", result.getCorrectedText());
        verify(repository, times(1)).save(taskEntity);
        verify(textCorrectionProcessor, times(1))
                .correct(taskEntity.getOriginalText(), taskEntity.getLanguage());
    }

}