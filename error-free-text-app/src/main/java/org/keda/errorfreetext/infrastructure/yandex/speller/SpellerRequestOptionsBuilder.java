package org.keda.errorfreetext.infrastructure.yandex.speller;

import org.springframework.stereotype.Component;

@Component
class SpellerRequestOptionsBuilder {

    public static final int IGNORE_DIGITS = 2;
    public static final int IGNORE_URLS = 4;
    public static final int FIND_REPEAT_WORDS = 8;
    public static final int IGNORE_CAPITALIZATION = 512;

    SpellerRequestOptionsBuilder() {
    }

    int build(
            boolean ignoreDigits,
            boolean ignoreUrls,
            boolean findRepeatWords,
            boolean ignoreCapitalization
            ) {
        return (ignoreDigits ? IGNORE_DIGITS : 0)
                | (ignoreUrls ? IGNORE_URLS : 0)
                | (findRepeatWords ? FIND_REPEAT_WORDS : 0)
                | (ignoreCapitalization ? IGNORE_CAPITALIZATION : 0);
    }
}
