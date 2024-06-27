package it.pagopa.pn.cucumber.steps.legalfact;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactCategory;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.Assertions;

public class LegalFactPADownloadClient implements LegalFactClient {
    @Override
    public LegalFactDownloadMetadataResponse getLegalFact(SharedSteps sharedSteps, LegalFactCategory categorySearch, String finalKeySearch) {
        return Assertions.assertDoesNotThrow(() -> sharedSteps.getB2bClient().getDownloadLegalFact(
                sharedSteps.getSentNotification().getIun(), finalKeySearch));
    }
}
