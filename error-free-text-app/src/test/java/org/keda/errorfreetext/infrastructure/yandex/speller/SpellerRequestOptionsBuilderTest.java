package org.keda.errorfreetext.infrastructure.yandex.speller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpellerRequestOptionsBuilderTest {

    private final SpellerRequestOptionsBuilder builder = new SpellerRequestOptionsBuilder();

    @Test
    void shouldReturnZeroWhenAllFlagsAreFalse() {
        int result = builder.build(
                false,
                false,
                false,
                false
        );

        assertEquals(0, result);
    }

    @Test
    void shouldReturnAllFlagsWhenAllFlagsAreTrue() {
        int result = builder.build(
                true,
                true,
                true,
                true
        );

        int expected = SpellerRequestOptionsBuilder.IGNORE_DIGITS
                + SpellerRequestOptionsBuilder.IGNORE_URLS
                + SpellerRequestOptionsBuilder.FIND_REPEAT_WORDS
                + SpellerRequestOptionsBuilder.IGNORE_CAPITALIZATION;

        assertEquals(expected, result);
    }

    @Test
    void shouldReturnIgnoreDigitsAndIgnoreUrlsOnly() {
        int result = builder.build(
                true,
                true,
                false,
                false
        );

        int expected = SpellerRequestOptionsBuilder.IGNORE_DIGITS
                + SpellerRequestOptionsBuilder.IGNORE_URLS;

        assertEquals(expected, result);
    }

}