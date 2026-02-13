package org.keda.errorfreetext;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@EnableAsync
public class ErrorFreeTextApplication {
    public static void main(String[] args) {
        SpringApplication.run(ErrorFreeTextApplication.class, args);
    }
}