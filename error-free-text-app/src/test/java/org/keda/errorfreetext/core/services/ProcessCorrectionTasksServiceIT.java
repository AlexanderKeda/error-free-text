package org.keda.errorfreetext.core.services;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.keda.errorfreetext.core.domain.TaskStatus;
import org.keda.errorfreetext.core.repositories.CorrectionTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
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

    @Test
    void shouldNotCrash() {
        tasksService.processNewTasks();
        var taskOpt = repository.findByTaskUuid(UUID.fromString("96e46569-5729-40d1-a707-3628b1ca53b1"));
        assertTrue(taskOpt.isPresent());
        assertEquals(TaskStatus.ERROR, taskOpt.get().getTaskStatus());
    }

}