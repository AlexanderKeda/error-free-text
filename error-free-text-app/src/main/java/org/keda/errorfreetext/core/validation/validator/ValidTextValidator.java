package org.keda.errorfreetext.core.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.keda.errorfreetext.core.validation.annotation.ValidText;

import java.util.regex.Pattern;

public class ValidTextValidator implements ConstraintValidator<ValidText, String> {

    private static final Pattern LETTER_PATTERN = Pattern.compile("[A-Za-zА-Яа-я]");

    @Override
    public boolean isValid(String text, ConstraintValidatorContext constraintValidatorContext) {
        if (text == null) {
            return true;
        }
        String trimmed = text.trim();
        return isLengthValid(trimmed) && containsLetter(trimmed);
    }

    private boolean isLengthValid(String s) {
        return s.length() >= 3;
    }

    private boolean containsLetter(String s) {
        return LETTER_PATTERN.matcher(s).find();
    }
}
