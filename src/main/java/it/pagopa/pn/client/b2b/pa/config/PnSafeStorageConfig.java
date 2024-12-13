package it.pagopa.pn.client.b2b.pa.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class PnSafeStorageConfig {

    @Value("${pn.safeStorage.apikey}")
    String apiKeySafeStorage;
    @Value("${pn.safeStorage.clientId}")
    String clientIdSafeStorage;
}
