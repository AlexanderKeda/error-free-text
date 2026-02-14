package org.keda.errorfreetext.infrastructure.yandex.speller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.keda.errorfreetext.properties.YandexSpellerProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class YandexSpellerClient {

    private static final ParameterizedTypeReference<List<List<SpellerCorrection>>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;
    private final YandexSpellerProperties spellerProperties;

    @Retryable(
            maxAttemptsExpression = "${yandex.speller.retry-max-attempts:3}",
            backoff = @Backoff(
                    delayExpression = "${yandex.speller.retry-delay:1000}",
                    multiplierExpression = "${yandex.speller.retry-multiplier:2}"
            )
    )
    public List<List<SpellerCorrection>> checkTexts(List<String> texts, String lang, int options) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        for (String text : texts) {
            body.add("text", text);
        }
        body.add("lang", lang);
        body.add("options", String.valueOf(options));

        return restClient.post()
                .uri(spellerProperties.checkTextsUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(RESPONSE_TYPE);
    }
}
