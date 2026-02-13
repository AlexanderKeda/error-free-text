package org.keda.errorfreetext.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;

import java.util.concurrent.Executors;

@Configuration
public class VirtualThreadExecutorConfig {

    @Bean
    TaskExecutor virtualCorrectionTasksExecutor() {
        return new TaskExecutorAdapter(
                Executors.newThreadPerTaskExecutor(
                        Thread.ofVirtual().name("virtual-task-processor-", 1).factory()
                )
        );
    }
}
