package org.keda.errorfreetext.core.services;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.keda.errorfreetext.core.domain.CorrectionTaskEntity;
import org.keda.errorfreetext.core.domain.Language;
import org.keda.errorfreetext.core.domain.TaskStatus;
import org.keda.errorfreetext.core.repositories.CorrectionTaskRepository;
import org.keda.errorfreetext.properties.YandexSpellerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@AutoConfigureWireMock(port = 8085)
class ProcessCorrectionTasksServiceIT {

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private ProcessCorrectionTasksService tasksService;

    @Autowired
    private CorrectionTaskRepository repository;

    @Autowired
    private YandexSpellerProperties yandexSpellerProperties;

    @Test
    void shouldProcessCorrectionTaskSuccessfullyWhenTextIsCorrect() {
        String originalText = "text";
        var newTask = CorrectionTaskEntity.builder()
                .taskUuid(UUID.randomUUID())
                .originalText(originalText)
                .language(Language.EN)
                .taskStatus(TaskStatus.NEW)
                .build();
        repository.save(newTask);

        wireMockServer.stubFor(WireMock.post("/checkTexts")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[[]]")));

        tasksService.processNewTasks();


        var taskOpt = repository.findByTaskUuid(newTask.getTaskUuid());
        assertTrue(taskOpt.isPresent());
        var doneTask = taskOpt.get();
        assertEquals(TaskStatus.DONE, doneTask.getTaskStatus());
        assertEquals(originalText, doneTask.getCorrectedText());
    }

    @Test
    void shouldProcessCorrectionTaskSuccessfullyWhenTextHasError() {
        String originalText = "Helo!";
        String correctedText = "Hello!";
        var newTask = CorrectionTaskEntity.builder()
                .taskUuid(UUID.randomUUID())
                .originalText(originalText)
                .language(Language.EN)
                .taskStatus(TaskStatus.NEW)
                .build();
        repository.save(newTask);

        wireMockServer.stubFor(WireMock.post("/checkTexts")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[[{\"code\": 1,\"pos\": 0,\"row\": 0,\"col\": 0,\"len\": 4,\"word\": \"Helo\",\"s\": [\"Hello\",\"Halo\",\"Help\"]}]]")));

        tasksService.processNewTasks();


        var taskOpt = repository.findByTaskUuid(newTask.getTaskUuid());
        assertTrue(taskOpt.isPresent());
        var doneTask = taskOpt.get();
        assertEquals(TaskStatus.DONE, doneTask.getTaskStatus());
        assertEquals(correctedText, doneTask.getCorrectedText());
    }

    @Test
    void shouldProcessCorrectionTaskWithErrorWhenApiResponseIsHttp500() {
        String originalText = "text";
        var newTask = CorrectionTaskEntity.builder()
                .taskUuid(UUID.randomUUID())
                .originalText(originalText)
                .language(Language.EN)
                .taskStatus(TaskStatus.NEW)
                .build();
        repository.save(newTask);

        wireMockServer.stubFor(WireMock.post("/checkTexts")
                .willReturn(WireMock.aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));

        tasksService.processNewTasks();

        var taskOpt = repository.findByTaskUuid(newTask.getTaskUuid());
        assertTrue(taskOpt.isPresent());
        var doneTask = taskOpt.get();
        assertEquals(TaskStatus.ERROR, doneTask.getTaskStatus());
        assertNull(doneTask.getCorrectedText());
        assertFalse(doneTask.getErrorMessage().isBlank());
    }

    @Test
    void shouldRetryWhenApiRespondsHttp500() {
        int maxRetryAttempts = yandexSpellerProperties.retryMaxAttempts();
        String originalText = "text";
        var newTask = CorrectionTaskEntity.builder()
                .taskUuid(UUID.randomUUID())
                .originalText(originalText)
                .language(Language.EN)
                .taskStatus(TaskStatus.NEW)
                .build();
        repository.save(newTask);

        wireMockServer.stubFor(WireMock.post("/checkTexts")
                .willReturn(WireMock.aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));

        tasksService.processNewTasks();

       wireMockServer.verify(maxRetryAttempts,WireMock.postRequestedFor(WireMock.urlEqualTo("/checkTexts")));
    }

    @Test
    void shouldProcessCorrectionTaskWithErrorWhenApiResponseTimesOut() {
        int readTimeout = yandexSpellerProperties.readTimeout();
        String originalText = "text";
        var newTask = CorrectionTaskEntity.builder()
                .taskUuid(UUID.randomUUID())
                .originalText(originalText)
                .language(Language.EN)
                .taskStatus(TaskStatus.NEW)
                .build();
        repository.save(newTask);

        wireMockServer.stubFor(WireMock.post("/checkTexts")
                .willReturn(WireMock.aResponse()
                        .withFixedDelay(readTimeout * 2)
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[[]]")));

        tasksService.processNewTasks();

        var taskOpt = repository.findByTaskUuid(newTask.getTaskUuid());
        assertTrue(taskOpt.isPresent());
        var doneTask = taskOpt.get();
        assertEquals(TaskStatus.ERROR, doneTask.getTaskStatus());
        assertNull(doneTask.getCorrectedText());
        assertFalse(doneTask.getErrorMessage().isBlank());
    }

    @Test
    void shouldProcessLongCorrectTextSuccessfully() {
        String originalText = readFromFile("/test-data/large-text.txt");
        var newTask = CorrectionTaskEntity.builder()
                .taskUuid(UUID.randomUUID())
                .originalText(originalText)
                .language(Language.RU)
                .taskStatus(TaskStatus.NEW)
                .build();
        repository.save(newTask);

        wireMockServer.stubFor(WireMock.post("/checkTexts")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[[],[]]")));

        tasksService.processNewTasks();


        var taskOpt = repository.findByTaskUuid(newTask.getTaskUuid());
        assertTrue(taskOpt.isPresent());
        var doneTask = taskOpt.get();
        assertEquals(TaskStatus.DONE, doneTask.getTaskStatus());
        assertEquals(originalText, doneTask.getCorrectedText());
    }

    private String readFromFile(String filePath) {
        try {
            Path path = new ClassPathResource(filePath).getFile().toPath();
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file from classpath: " + filePath, e);
        }
    }

}