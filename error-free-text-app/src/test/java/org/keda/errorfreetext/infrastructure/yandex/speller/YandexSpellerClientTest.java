package org.keda.errorfreetext.infrastructure.yandex.speller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keda.errorfreetext.properties.YandexSpellerProperties;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YandexSpellerClientTest {

    @Mock
    private RestClient restClient;

    @Mock
    private YandexSpellerProperties properties;

    @InjectMocks
    private YandexSpellerClient client;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private static final ParameterizedTypeReference<List<List<SpellerCorrection>>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    @Test
    void shouldCallSpellerApiWithCorrectParameters() {
        List<String> texts = List.of("text1");
        String lang = "en";
        int options = 2;

        when(properties.checkTextsUri()).thenReturn("/check");
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/check")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(MultiValueMap.class)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(RESPONSE_TYPE))
                .thenReturn(List.of());

        client.checkTexts(texts, lang, options);

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("/check");
        verify(requestBodySpec).contentType(MediaType.APPLICATION_FORM_URLENCODED);
        verify(requestBodySpec).body(any(MultiValueMap.class));
        verify(requestBodySpec).retrieve();
        verify(responseSpec).body(RESPONSE_TYPE);
    }

    @Test
    void shouldReturnResponseFromRestClient() {
        List<List<SpellerCorrection>> expected = List.of(List.of());

        when(properties.checkTextsUri()).thenReturn("/check");
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(MultiValueMap.class)))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(RESPONSE_TYPE))
                .thenReturn(expected);

        List<List<SpellerCorrection>> result =
                client.checkTexts(List.of("text"), "en", 0);

        assertSame(expected, result);
    }

}