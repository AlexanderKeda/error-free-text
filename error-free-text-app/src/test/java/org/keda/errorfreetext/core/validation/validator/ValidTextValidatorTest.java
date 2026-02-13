package org.keda.errorfreetext.core.validation.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ValidTextValidatorTest {

    private ValidTextValidator validator = new ValidTextValidator();

    @Mock
    private ConstraintValidatorContext context;

    @ParameterizedTest
    @ValueSource(strings = {"Hello", "Привет", "abc123", "  Text  ", "  хор  ", "put"})
    void shouldPassWhenTextContainsLettersAndLengthValid(String text) {
        boolean result = validator.isValid(text, context);
        assertTrue(result);
    }

    @Test
    void shouldPassWhenTextIsNull() {
        boolean result = validator.isValid(null, context);
        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  Hi", "да  ", "О", "  go  ", "  хм  ", "a","12%^", "52346", "(%@&#%)"})
    void shouldFailWhenTextIsTooShortOrTextContainsNoLetters(String text) {
        boolean result = validator.isValid(text, context);
        assertFalse(result);
    }

}