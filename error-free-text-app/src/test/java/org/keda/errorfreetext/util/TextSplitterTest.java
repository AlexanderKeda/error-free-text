package org.keda.errorfreetext.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.keda.errorfreetext.properties.TextSplitterProperties;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TextSplitterTest {

    @Mock
    private TextSplitterProperties properties;

    @InjectMocks
    private TextSplitter splitter;

    @ParameterizedTest
    @ValueSource(strings = {"Hello", "abcdefghij", ""})
    void shouldReturnSingleChunkWhenTextLengthLessOrEqualsThanMaxChunkSize(String text) {
        when(properties.maxChunkSize()).thenReturn(10);
        List<String> chunks = splitter.split(text);
        assertEquals(1, chunks.size());
        assertEquals(text, chunks.getFirst());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc def ghi jkl mno pqr", "abcdefghijklmno"})
    void shouldSplitTextIntoChunksWhenTextLengthGreaterThanMaxChunkSize(String text) {
        when(properties.maxChunkSize()).thenReturn(10);
        List<String> chunks = splitter.split(text);

        for (String chunk : chunks) {
            assertTrue(chunk.length() <= 10);
        }

        String reconstructed = String.join("", chunks);
        assertEquals(text, reconstructed);
    }

    @Test
    void shouldReturnEmptyListWhenTextIsNull() {
        String text = null;
        List<String> chunks = splitter.split(text);
        assertTrue(chunks.isEmpty());
    }
}