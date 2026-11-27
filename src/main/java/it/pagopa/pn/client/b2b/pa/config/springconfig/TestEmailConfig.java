package it.pagopa.pn.client.b2b.pa.config.springconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Casella email dei test e credenziali OAuth 2.0 per leggerla tramite Gmail API (scope gmail.readonly).
 * <p>
 * Valori in {@code config/test-email-{env}.properties}: l'indirizzo in chiaro, le credenziali come riferimento
 * alle chiavi {@code e2eTestEmailClientId}, {@code e2eTestEmailClientSecret} ed {@code e2eTestEmailRefreshToken}
 * del secret {@code secretsForTests}. In pipeline le passa pn-cicd, in locale vanno in
 * {@code config/api-keys-{env}.properties}. Non vanno mai scritte nei log.
 */
@Getter
@Setter
@Component
@PropertySource("file:config/test-email-${spring.profiles.active}.properties")
@ConfigurationProperties(prefix = "pn.test.email")
public class TestEmailConfig {
    private String address;
    private String clientId;
    private String clientSecret;
    private String refreshToken;
}
