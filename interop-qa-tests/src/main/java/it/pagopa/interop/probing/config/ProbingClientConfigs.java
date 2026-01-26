package it.pagopa.interop.probing.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pn.interop.probing", ignoreUnknownFields = false)
@Getter
@Setter
public class ProbingClientConfigs {
    private String baseUrl;
    private String bearerTokenKms;
    private String bearerTokenTelemetry;
}