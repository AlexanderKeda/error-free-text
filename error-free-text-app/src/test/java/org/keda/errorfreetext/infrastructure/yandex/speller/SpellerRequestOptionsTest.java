package org.keda.errorfreetext.infrastructure.yandex.speller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpellerRequestOptionsTest {

    @Test
    void shouldReturnZeroWhenAllFlagsAreFalse() {
        int result = SpellerRequestOptions.buildOptions(
                false,
                false,
                false,
                false
        );

        assertEquals(0, result);
    }

    @Test
    void shouldReturnAllFlagsWhenAllFlagsAreTrue() {
        int result = SpellerRequestOptions.buildOptions(
                true,
                true,
                true,
                true
        );

        int expected = SpellerRequestOptions.IGNORE_DIGITS
                + SpellerRequestOptions.IGNORE_URLS
                + SpellerRequestOptions.FIND_REPEAT_WORDS
                + SpellerRequestOptions.IGNORE_CAPITALIZATION;

        assertEquals(expected, result);
    }

    @Test
    void shouldReturnIgnoreDigitsAndIgnoreUrlsOnly() {
        int result = SpellerRequestOptions.buildOptions(
                true,
                true,
                false,
                false
        );

        int expected = SpellerRequestOptions.IGNORE_DIGITS
                + SpellerRequestOptions.IGNORE_URLS;

        assertEquals(expected, result);
    }

}