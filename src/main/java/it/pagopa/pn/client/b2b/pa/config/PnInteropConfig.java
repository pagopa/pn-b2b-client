package it.pagopa.pn.client.b2b.pa.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class PnInteropConfig {

    @Value("${pn.interop.token-oauth2.path}")
    private String tokenOauth2Path;
    @Value("${pn.interop.token-oauth2.client-assertion}")
    private String clientAssertion;
    @Value("${pn.interop.clientId}")
    private String interopClientId;
    @Value("${pn.interop.enable}")
    private String enableInterop;
}
