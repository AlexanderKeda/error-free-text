package org.keda.errorfreetext.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.keda.errorfreetext.properties.YandexSpellerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@EnableRetry
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class YandexSpellerClientConfig {

    private final YandexSpellerProperties spellerProperties;

    @Bean
    public RestClient yandexSpellerRestClient(RestClient.Builder builder) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(spellerProperties.connectionTimeout()))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(spellerProperties.readTimeout());

        return builder
                .requestFactory(factory)
                .baseUrl(spellerProperties.baseUrl())
                .build();
    }

}
