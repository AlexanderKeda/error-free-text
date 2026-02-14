package org.keda.errorfreetext.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "text.splitter")
public record TextSplitterProperties(
        int maxChunkSize
) {
}
