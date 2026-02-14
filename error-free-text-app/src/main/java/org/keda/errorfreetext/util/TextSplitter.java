package org.keda.errorfreetext.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.keda.errorfreetext.properties.TextSplitterProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TextSplitter {

    private final TextSplitterProperties properties;

    public List<String> split(String text) {
        if (text == null) {
            return List.of();
        } else if (text.length() <= properties.maxChunkSize()) {
            return List.of(text);
        }
        return splitIntoChunks(text);
    }

    private List<String> splitIntoChunks(String text) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + properties.maxChunkSize(), text.length());
            String chunk = text.substring(start, end);
            int lastWhiteSpace = lastWhitespaceIndex(chunk);
            if (lastWhiteSpace != -1 && lastWhiteSpace != 0 && end != text.length()) {
                chunk = chunk.substring(0, lastWhiteSpace);
            }
                chunks.add(chunk);
                start += chunk.length();
        }
        return chunks;
    }

    private int lastWhitespaceIndex(String text) {
        for (int i = text.length() - 1; i >= 0; i--) {
            if (Character.isWhitespace(text.charAt(i))) {
                return i;
            }
        }
        return -1;
    }
}
