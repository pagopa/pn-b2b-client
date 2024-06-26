package it.pagopa.pn.cucumber.steps.legalfact;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactCategory;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.legalfact.data.LegalFactClientType;

import java.util.List;

public interface LegalFactClient {

    LegalFactDownloadMetadataResponse getLegalFact(final List<LegalFactClientType> legalFactClientTypes, final SharedSteps sharedSteps, final LegalFactCategory categorySearch, final String finalKeySearch);

}
