package it.pagopa.pn.client.b2b.pa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "pn.OpenSearch", ignoreUnknownFields = true)
@Data
public class PnOpenSearchConfig {

    private String username;
    private String password;
}
