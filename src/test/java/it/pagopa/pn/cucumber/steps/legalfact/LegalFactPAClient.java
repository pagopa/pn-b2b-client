package it.pagopa.pn.cucumber.steps.legalfact;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactCategory;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Component;

@Component
public class LegalFactPAClient implements LegalFactClient {
    @Override
    public LegalFactDownloadMetadataResponse getLegalFact(final SharedSteps sharedSteps, final LegalFactCategory categorySearch, final String finalKeySearch) {
        IPnPaB2bClient b2bClient = sharedSteps.getB2bClient();
        return Assertions.assertDoesNotThrow(() -> b2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch, finalKeySearch));
    }

}
