package it.pagopa.pn.client.b2b.pa.interop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "interop.configuration", ignoreUnknownFields = false)
@Data
public class InteropClientConfigs {
    private String basePath;
    private String bearerToken;
}
