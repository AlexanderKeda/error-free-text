package org.keda.errorfreetext.infrastructure.yandex.speller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class SpellerCorrectionApplier {

    String apply(String text, List<SpellerCorrection> corrections) {
        if (corrections == null || corrections.isEmpty()) {
            return text;
        }
        var sortCorrections = filterAndSortErrorsDescending(corrections);
        return applyCorrections(text, sortCorrections);
    }

    private List<SpellerCorrection> filterAndSortErrorsDescending(List<SpellerCorrection> corrections) {
        return corrections.stream()
                .filter(error -> error.s() != null && !error.s().isEmpty())
                .sorted(Comparator.comparingInt(SpellerCorrection::pos).reversed())
                .toList();
    }

    private String applyCorrections(String text, List<SpellerCorrection> corrections) {
        StringBuilder builder = new StringBuilder(text);
        for (SpellerCorrection error : corrections) {
            int start = error.pos();
            int end = start + error.len();
            if (start < 0 || end > builder.length() || start >= end) {
                continue;
            }
            builder.replace(start, end, error.s().getFirst());
        }
        return builder.toString();
    }
}
