package it.pagopa.pn.client.b2b.pa.springconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource( value = "file:config/bearer-token.properties", ignoreResourceNotFound = true )
public class BearerTokenConfiguration {
}