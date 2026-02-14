package org.keda.errorfreetext.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keda.errorfreetext.core.api.command.CorrectionTaskResult;
import org.keda.errorfreetext.core.api.command.CorrectionTaskResultQuery;
import org.keda.errorfreetext.core.api.command.CreateCorrectionTaskCommand;
import org.keda.errorfreetext.core.api.command.CreateCorrectionTaskResult;
import org.keda.errorfreetext.core.services.CreateCorrectionTaskService;
import org.keda.errorfreetext.core.services.GetCorrectionTaskResultService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
public class CorrectionTaskController {

    private final CreateCorrectionTaskService createCorrectionTaskService;
    private final GetCorrectionTaskResultService getCorrectionTaskResultService;

    @PostMapping(path = "",
            consumes = "application/json",
            produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCorrectionTaskResult create(@RequestBody @Valid CreateCorrectionTaskCommand command) {
        log.info("Received request to create correction task");
        log.info("Request to create: {}", command);
        var response = createCorrectionTaskService.create(command);
        log.info("Response on create request: {}", response);
        return response;
    }

    @GetMapping("/{uuid}")
    public CorrectionTaskResult getCorrectionTaskResult(@PathVariable UUID uuid) {
        log.info("Request to get result: {}", uuid);
        var query = new CorrectionTaskResultQuery(uuid);
        var response = getCorrectionTaskResultService.get(query);
        log.info("Response on get request: {}", response);
        return response;
    }

}
