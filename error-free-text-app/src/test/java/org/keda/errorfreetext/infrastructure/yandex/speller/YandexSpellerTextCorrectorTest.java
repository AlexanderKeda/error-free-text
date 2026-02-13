package org.keda.errorfreetext.infrastructure.yandex.speller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.keda.errorfreetext.core.domain.Language;
import org.keda.errorfreetext.util.TextSplitter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YandexSpellerTextCorrectorTest {

    @Mock
    private TextSplitter textSplitter;

    @Mock
    private SpellerRequestOptionsBuilder requestOptionsBuilder;

    @Mock
    private YandexSpellerClient spellerClient;

    @Mock
    private SpellerCorrectionApplier correctionApplier;

    @InjectMocks
    private YandexSpellerTextCorrector corrector;

    @Test
    void shouldThrowExceptionWhenTextIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> corrector.correct(null, Language.EN)
        );
        assertEquals("Text to correct cannot be null or blank", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTextIsBlank() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> corrector.correct("   ", Language.EN)
        );
        assertEquals("Text to correct cannot be null or blank", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLanguageIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> corrector.correct("Hello", null)
        );
        assertEquals("Language cannot be null", ex.getMessage());
    }

    @Test
    void shouldSetIgnoreDigitsFalseAndIgnoreUrlsFalse() {
        String text = "Hello";
        List<String> chunks = List.of(text);
        when(textSplitter.split(text)).thenReturn(chunks);
        when(spellerClient.checkTexts(any(), any(), anyInt())).thenReturn(List.of(List.of()));
        when(requestOptionsBuilder.build(false, false, false, false))
                .thenReturn(0);

        corrector.correct(text, Language.EN);
        verify(requestOptionsBuilder).build(false, false, false, false);
    }

    @Test
    void shouldSetIgnoreDigitsTrue() {
        String text = "Hello123";
        List<String> chunks = List.of(text);
        when(textSplitter.split(text)).thenReturn(chunks);
        when(spellerClient.checkTexts(any(), any(), anyInt())).thenReturn(List.of(List.of()));
        when(requestOptionsBuilder.build(true, false, false, false))
                .thenReturn(2);

        corrector.correct(text, Language.EN);
        verify(requestOptionsBuilder).build(true, false, false, false);
    }

    @ParameterizedTest
    @ValueSource(strings = {"www.google.com", "https://yandex.by/"})
    void shouldSetIgnoreUrlsTrue(String text) {
        List<String> chunks = List.of(text);
        when(textSplitter.split(text)).thenReturn(chunks);
        when(spellerClient.checkTexts(any(), any(), anyInt())).thenReturn(List.of(List.of()));
        when(requestOptionsBuilder.build(false, true, false, false))
                .thenReturn(4);

        corrector.correct(text, Language.EN);
        verify(requestOptionsBuilder).build(false, true, false, false);
    }

    @Test
    void shouldThrowWhenResponseIsNull() {
        String text = "Hello";
        List<String> chunks = List.of(text);
        when(textSplitter.split(text)).thenReturn(chunks);
        when(spellerClient.checkTexts(any(), any(), anyInt())).thenReturn(null);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> corrector.correct(text, Language.EN)
        );
        assertEquals("Empty response from yandex speller service", ex.getMessage());
    }

    @Test
    void shouldThrowWhenResponseSizeMismatch() {
        String text = "Hello world";
        List<String> chunks = List.of("Hello", " world");
        when(textSplitter.split(text)).thenReturn(chunks);
        when(spellerClient.checkTexts(any(), any(), anyInt())).thenReturn(List.of(List.of()));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> corrector.correct(text, Language.EN)
        );
        assertEquals("Unexpected response size yandex from speller service", ex.getMessage());
    }

    @Test
    void shouldCombineMultipleCorrectedChunksIntoSingleText() {
        String text = "Hello World";
        List<String> chunks = List.of("Hello", " World");
        List<List<SpellerCorrection>> response = List.of(
                List.of(),
                List.of()
        );

        when(textSplitter.split(text)).thenReturn(chunks);
        when(spellerClient.checkTexts(any(), any(), anyInt())).thenReturn(response);
        when(correctionApplier.apply("Hello", response.get(0))).thenReturn("Hello");
        when(correctionApplier.apply(" World", response.get(1))).thenReturn(" World");

        String result = corrector.correct(text, Language.EN);

        assertEquals("Hello World", result);
    }
}