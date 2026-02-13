package org.keda.errorfreetext.infrastructure.yandex.speller;

import java.util.List;

record SpellerError(
        int code,
        int pos,
        int row,
        int col,
        int len,
        String word,
        List<String> s
) {
}
