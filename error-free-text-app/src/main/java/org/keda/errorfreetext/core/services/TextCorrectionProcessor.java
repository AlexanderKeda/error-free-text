package org.keda.errorfreetext.core.services;

import org.keda.errorfreetext.core.domain.Language;

public interface TextCorrectionProcessor {

    String correct(String text, Language language);

}
