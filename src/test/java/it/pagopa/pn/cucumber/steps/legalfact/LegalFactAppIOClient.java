package it.pagopa.pn.cucumber.steps.legalfact;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactCategory;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.springframework.stereotype.Component;

@Component
public class LegalFactAppIOClient implements LegalFactClient {

    @Override
    public LegalFactDownloadMetadataResponse getLegalFact(final SharedSteps sharedSteps, LegalFactCategory categorySearch, String finalKeySearch) {
        return null;
    }
}
