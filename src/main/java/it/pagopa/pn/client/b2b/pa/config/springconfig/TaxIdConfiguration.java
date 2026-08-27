package it.pagopa.pn.client.b2b.pa.config.springconfig;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Carica {@code config/taxids-{spring.profiles.active}.properties} e abilita il binding su
 * {@link RecipientConfig}, unica fonte dei dati anagrafici (tax-id, uid) delle utenze di test.
 */
@Configuration
@PropertySource("file:config/taxids-${spring.profiles.active}.properties")
@EnableConfigurationProperties(RecipientConfig.class)
public class TaxIdConfiguration {
}
