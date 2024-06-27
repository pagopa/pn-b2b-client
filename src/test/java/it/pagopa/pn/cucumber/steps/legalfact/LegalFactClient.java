package it.pagopa.pn.cucumber.steps.legalfact;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactCategory;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;

public interface LegalFactClient {
    
    LegalFactDownloadMetadataResponse getLegalFact(final SharedSteps sharedSteps, final LegalFactCategory categorySearch, final String finalKeySearch);

}
