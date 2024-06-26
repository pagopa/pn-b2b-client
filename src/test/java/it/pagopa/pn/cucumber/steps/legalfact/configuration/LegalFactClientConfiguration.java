package it.pagopa.pn.cucumber.steps.legalfact.configuration;

import it.pagopa.pn.cucumber.steps.legalfact.LegalFactAppIOClient;
import it.pagopa.pn.cucumber.steps.legalfact.LegalFactClient;
import it.pagopa.pn.cucumber.steps.legalfact.LegalFactPAClient;
import it.pagopa.pn.cucumber.steps.legalfact.LegalFactWebRecipientClient;
import it.pagopa.pn.cucumber.steps.legalfact.data.LegalFactClientType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class LegalFactClientConfiguration {

    @Bean
    List<LegalFactClient> legalFactClients() {
        return List.of(new LegalFactAppIOClient(),
                new LegalFactWebRecipientClient(),
                new LegalFactPAClient());
    }

    @Bean
    Map<LegalFactClientType, LegalFactClient> legalFactClientMap() {
        return Map.of(LegalFactClientType.PA, new LegalFactPAClient(),
                LegalFactClientType.APP_IO, new LegalFactAppIOClient(),
                LegalFactClientType.WEB_RECIPIENT, new LegalFactWebRecipientClient());
    }
}
