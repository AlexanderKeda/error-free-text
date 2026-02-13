package org.keda.errorfreetext.infrastructure.yandex.speller;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpellerCorrectionApplierTest {

    private final SpellerCorrectionApplier applier = new SpellerCorrectionApplier();

    @Test
    void shouldReturnOriginalTextWhenCorrectionsNull() {
        String text = "Hello";
        var result = applier.apply(text, null);
        assertEquals("Hello", result);
    }

    @Test
    void shouldReturnOriginalTextWhenCorrectionsEmpty() {
        String text = "Hello";
        var result = applier.apply(text, List.of());
        assertEquals("Hello", result);
    }

    @Test
    void shouldApplySingleCorrection() {
        String text = "Helo";
        SpellerCorrection c = correctionCreate(0, 4, "Hello");
        var result = applier.apply(text, List.of(c));
        assertEquals("Hello", result);
    }

    @Test
    void shouldApplyMultipleCorrectionsInReverseOrder() {
        String text = "Helo Word";
        SpellerCorrection c1 = correctionCreate(0, 4, "Hello");
        SpellerCorrection c2 = correctionCreate(5, 4, "World");
        assertEquals("Hello World", applier.apply(text, List.of(c1, c2)));
    }

    @Test
    void shouldIgnoreCorrectionWithEmptyReplacementList() {
        String text = "Helo Wrold";
        SpellerCorrection c = new SpellerCorrection(
                0,
                0,
                0,
                0,
                4,
                "",
                List.of()
        );
        assertEquals("Helo Wrold", applier.apply(text, List.of(c)));
    }

    @Test
    void shouldIgnoreCorrectionWithNegativePosition() {
        String text = "Helo";
        SpellerCorrection c = correctionCreate(-1, 3, "Hi");
        assertEquals("Helo", applier.apply(text, List.of(c)));
    }

    @Test
    void shouldIgnoreCorrectionExceedingTextLength() {
        String text = "Helo";
        SpellerCorrection c = correctionCreate(1, 10, "Hi");
        assertEquals("Helo", applier.apply(text, List.of(c)));
    }

    private SpellerCorrection correctionCreate(int pos, int len, String replacement) {
        return new SpellerCorrection(
                0,
                pos,
                0,
                0,
                len,
                "",
                List.of(replacement)
        );
    }

}