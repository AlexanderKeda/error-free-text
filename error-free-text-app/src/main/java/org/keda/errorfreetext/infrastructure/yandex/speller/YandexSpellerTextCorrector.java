package org.keda.errorfreetext.infrastructure.yandex.speller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.keda.errorfreetext.core.domain.Language;
import org.keda.errorfreetext.core.services.TextCorrectionProcessor;
import org.keda.errorfreetext.util.TextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class YandexSpellerTextCorrector implements TextCorrectionProcessor {

    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern URL_PATTERN = Pattern.compile("(?i)\\b((https?://|www\\.)\\S+)");
    private final TextSplitter textSplitter;
    private final SpellerRequestOptionsBuilder requestOptionsBuilder;
    private final YandexSpellerClient spellerClient;
    private final SpellerCorrectionApplier correctionApplier;

    @Override
    public String correct(String text, Language language) {
        validateInput(text, language);
        int requestOptions = buildRequestOptions(text);
        List<String> chunks = textSplitter.split(text);
        List<List<SpellerCorrection>> response = spellerClient.checkTexts(chunks, language.toString(), requestOptions);
        validateResponse(chunks, response);
        return buildCorrectedText(chunks, response);
    }

    private void validateInput(String text, Language language) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text to correct cannot be null or blank");
        } else if (language == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }
    }

    private void validateResponse(List<String> chunks, List<List<SpellerCorrection>> response) {
        if(response == null) {
            throw new IllegalStateException("Empty response from yandex speller service");
        } else if (response.size() != chunks.size()) {
            throw new IllegalStateException("Unexpected response size yandex from speller service");
        }
    }

    private int buildRequestOptions(String text) {
        boolean ignoreDigits = DIGIT_PATTERN.matcher(text).find();
        boolean ignoreUrls = URL_PATTERN.matcher(text).find();
        boolean findRepeatWords = false;
        boolean ignoreCapitalization = false;
        return requestOptionsBuilder.build(
                ignoreDigits,
                ignoreUrls,
                findRepeatWords,
                ignoreCapitalization
        );
    }

    private String buildCorrectedText(List<String> chunks, List<List<SpellerCorrection>> chunksCorrections) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            List<SpellerCorrection> corrections = chunksCorrections.get(i);
            String correctedChunk = correctionApplier.apply(chunk, corrections);
            builder.append(correctedChunk);
        }
        return builder.toString();
    }
}
