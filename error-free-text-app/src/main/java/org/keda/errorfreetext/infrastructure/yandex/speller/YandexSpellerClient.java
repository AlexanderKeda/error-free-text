package org.keda.errorfreetext.infrastructure.yandex.speller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
class YandexSpellerClient {

    private static final ParameterizedTypeReference<List<List<SpellerError>>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {};
    private final String baseUrl;
    private final String checkTextsUri;
    private final RestClient restClient;

    YandexSpellerClient(
            @Value("${yandex.speller.base-url}") String baseUrl,
            @Value("${yandex.speller.check-texts.uri}") String checkTextsUri
    ) {
        this.baseUrl = baseUrl;
        this.checkTextsUri = checkTextsUri;
        this.restClient = RestClient
                .builder()
                .baseUrl(baseUrl)
                .build();
    }

    List<List<SpellerError>> checkTexts(List<String> texts, String lang, int options) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        for (String text : texts) {
            body.add("text", text);
        }
        body.add("lang", lang);
        body.add("options", String.valueOf(options));

        return restClient.post()
                .uri(checkTextsUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(RESPONSE_TYPE);
    }
}
