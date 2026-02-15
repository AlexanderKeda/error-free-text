package org.keda.errorfreetext.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.keda.errorfreetext.core.api.command.CreateCorrectionTaskResult;
import org.keda.errorfreetext.core.domain.CorrectionTaskEntity;
import org.keda.errorfreetext.core.domain.Language;
import org.keda.errorfreetext.core.domain.TaskStatus;
import org.keda.errorfreetext.core.repositories.CorrectionTaskRepository;
import org.keda.errorfreetext.rest.dto.ErrorResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class CorrectionTaskControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CorrectionTaskRepository repository;

    @Test
    void get_shouldReturnHttp200AndResponseWithTaskStatusNew() throws Exception {
        UUID uuid = UUID.randomUUID();

        var task = CorrectionTaskEntity.builder()
                .taskUuid(uuid)
                .originalText("text")
                .language(Language.EN)
                .taskStatus(TaskStatus.NEW)
                .build();
        repository.save(task);

        mockMvc.perform(get("/tasks/{uuid}", uuid.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(uuid.toString()))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.correctedText").doesNotExist())
                .andExpect(jsonPath("$.errorMessage").doesNotExist());
    }

    @Test
    void get_shouldReturnHttp200AndResponseWithTaskStatusProcessing() throws Exception {
        UUID uuid = UUID.randomUUID();

        var task = CorrectionTaskEntity.builder()
                .taskUuid(uuid)
                .originalText("text")
                .language(Language.EN)
                .taskStatus(TaskStatus.PROCESSING)
                .build();
        repository.save(task);

        mockMvc.perform(get("/tasks/{uuid}", uuid.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(uuid.toString()))
                .andExpect(jsonPath("$.status").value("PROCESSING"))
                .andExpect(jsonPath("$.correctedText").doesNotExist())
                .andExpect(jsonPath("$.errorMessage").doesNotExist());
    }

    @Test
    void get_shouldReturnHttp200AndResponseWithTaskStatusDone() throws Exception {
        UUID uuid = UUID.randomUUID();

        var task = CorrectionTaskEntity.builder()
                .taskUuid(uuid)
                .originalText("text")
                .language(Language.EN)
                .correctedText("text")
                .taskStatus(TaskStatus.DONE)
                .build();
        repository.save(task);

        mockMvc.perform(get("/tasks/{uuid}", uuid.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(uuid.toString()))
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.correctedText").value("text"))
                .andExpect(jsonPath("$.errorMessage").doesNotExist());
    }

    @Test
    void get_shouldReturnHttp200AndResponseWithTaskStatusError() throws Exception {
        UUID uuid = UUID.randomUUID();

        var task = CorrectionTaskEntity.builder()
                .taskUuid(uuid)
                .originalText("text")
                .language(Language.EN)
                .taskStatus(TaskStatus.ERROR)
                .errorMessage("error")
                .build();
        repository.save(task);

        mockMvc.perform(get("/tasks/{uuid}", uuid.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(uuid.toString()))
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.correctedText").doesNotExist())
                .andExpect(jsonPath("$.errorMessage").value("error"));
    }

    @Test
    void get_shouldReturnHttp404AndResponseWithErrorDescriptionWhenUuidNotFound() throws Exception {
        String uuid = UUID.randomUUID().toString();
        String expectedErrorMessage = String.format( "Task with uuid: %s not found", uuid);
        String expectedPath = String.format( "/tasks/%s", uuid);

        mockMvc.perform(get("/tasks/{uuid}", uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value(expectedErrorMessage))
                .andExpect(jsonPath("$.errorCode").value(40401))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value(expectedPath));
    }
    @Test
    void get_shouldReturnHttp400AndResponseWithStatusError() throws Exception {
        String requestUuid = "123";
        String expectedErrorMessage = "Invalid request";
        String expectedPath = String.format( "/tasks/%s", requestUuid);

        mockMvc.perform(get("/tasks/{uuid}", requestUuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(expectedErrorMessage))
                .andExpect(jsonPath("$.errorCode").value(40001))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value(expectedPath));
    }

    @Test
    void post_shouldReturnHttp201AndCreateTaskAndResponseWithTaskUuid() throws Exception {
        String request = buildPostRequest("text", "EN");

        MvcResult result = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").exists())
                .andReturn();
        var response = objectMapper
                .readValue(result.getResponse().getContentAsString(), CreateCorrectionTaskResult.class);
        var createdTaskOpt = repository.findByTaskUuid(response.uuid());
        assertTrue(createdTaskOpt.isPresent());
    }

    @ParameterizedTest
    @CsvSource({
            "hi, EN",
            "124#%@&34, EN",
            " hi  , EN",
            "text, by",
            "text, 52yt245g2",
            "'', ''"
    })
    void post_shouldReturnHttp400AndResponseWithErrorDescriptionWhenRequestInvalid(
            String text, String language
    ) throws Exception {
        String request = buildPostRequest(text, language);
        String path = "/tasks";

        MvcResult result = mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").exists())
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value(path))
                .andReturn();
        var response = objectMapper
                .readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);
        assertTrue(response.errorCode() == 40001 || response.errorCode() == 40002);
    }

    private String buildPostRequest(String text, String language) {
        return String.format(
                """
                        {
                            "text": "%s",
                            "language": "%s"
                        }
                        """,
                text,
                language
        );
    }

}