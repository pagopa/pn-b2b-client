package it.pagopa.pn.client.b2b.pa.config.springconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource(value = "file:config/parser.properties", ignoreResourceNotFound = true)
public class LegalFactTextTokens {
}