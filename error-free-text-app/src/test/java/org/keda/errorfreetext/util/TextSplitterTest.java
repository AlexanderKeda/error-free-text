package org.keda.errorfreetext.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextSplitterTest {

    private TextSplitter splitter = new TextSplitter(10);

    @ParameterizedTest
    @ValueSource(strings = {"Hello", "abcdefghij", ""})
    void shouldReturnSingleChunkWhenTextLengthLessOrEqualsThanMaxChunkSize(String text) {
        List<String> chunks = splitter.split(text);
        assertEquals(1, chunks.size());
        assertEquals(text, chunks.get(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc def ghi jkl mno pqr", "abcdefghijklmno"})
    void shouldSplitTextIntoChunksWhenTextLengthGreaterThanMaxChunkSize(String text) {
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