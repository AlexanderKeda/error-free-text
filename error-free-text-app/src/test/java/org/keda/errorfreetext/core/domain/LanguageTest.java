package org.keda.errorfreetext.core.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LanguageTest {

    @Test
    void shouldReturnENWhenValueIsEnRegardlessOfCase() {
        assertEquals(Language.EN, Language.from("en"));
        assertEquals(Language.EN, Language.from("EN"));
        assertEquals(Language.EN, Language.from("eN"));
    }

    @Test
    void shouldReturnRUWhenValueIsRuRegardlessOfCase() {
        assertEquals(Language.RU, Language.from("ru"));
        assertEquals(Language.RU, Language.from("RU"));
        assertEquals(Language.RU, Language.from("rU"));
    }

    @Test
    void shouldTrimWhitespaceAroundValue() {
        assertEquals(Language.EN, Language.from("  en  "));
        assertEquals(Language.RU, Language.from("  ru  "));
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Language.from(null)
        );
        assertEquals("Cannot create Language: value is null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueIsUnsupported() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Language.from("FR")
        );
        assertEquals("Unsupported language: FR", exception.getMessage());
    }


}