package it.pagopa.interop.tracing.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pn.interop.tracing", ignoreUnknownFields = false)
@Getter
@Setter
public class TracingClientConfigs {
    private String baseUrl;
    private String bearerToken1;
    private String bearerToken2;
}
