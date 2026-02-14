package org.keda.errorfreetext.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "yandex.speller")
public record YandexSpellerProperties(
        String baseUrl,
        String checkTextsUri,
        int connectionTimeout,
        int readTimeout,
        int retryMaxAttempts,
        int retryDelay,
        int retryMultiplier
) {}
