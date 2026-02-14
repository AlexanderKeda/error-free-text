package org.keda.errorfreetext.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "correction.task.provider")
public record CorrectionTaskProviderProperties(
        int pageSize
) {
}
