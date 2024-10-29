package it.pagopa.pn.client.b2b.pa.interop.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pn.interop.tracing", ignoreUnknownFields = false)
@Getter
@Setter
public class InteropClientConfigs {
    private String baseUrl;
    private String bearerToken1;
}
