package it.pagopa.pn.client.b2b.pa.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


//@ConfigurationProperties(prefix = "pn.OpenSearch", ignoreUnknownFields = true)
@Configuration
@Data
public class PnOpenSearchConfig {

    @Value("${pn.OpenSearch.username}")
    private String username;
    @Value("${pn.OpenSearch.password}")
    private String password;
}
