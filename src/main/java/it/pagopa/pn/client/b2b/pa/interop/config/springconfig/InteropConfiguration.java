package it.pagopa.pn.client.b2b.pa.interop.config.springconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource( value = "file:config/interop-tracing-${spring.profiles.active}.properties", ignoreResourceNotFound = true )
public class InteropConfiguration {
}
