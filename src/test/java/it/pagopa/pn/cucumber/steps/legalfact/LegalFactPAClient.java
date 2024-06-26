package it.pagopa.pn.cucumber.steps.legalfact;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactCategory;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.legalfact.data.LegalFactClientType;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class LegalFactPAClient implements LegalFactClient {

    private static final LegalFactClientType LEGAL_FACT_PA_CLIENT_TYPE = LegalFactClientType.PA;

    @Override
    public LegalFactDownloadMetadataResponse getLegalFact(final List<LegalFactClientType> legalFactClientTypes, final SharedSteps sharedSteps, final LegalFactCategory categorySearch, final String finalKeySearch) {
        LegalFactDownloadMetadataResponse response = null;
        if (legalFactClientTypes.contains(LEGAL_FACT_PA_CLIENT_TYPE)) {
            IPnPaB2bClient b2bClient = sharedSteps.getB2bClient();
            response = Assertions.assertDoesNotThrow(() ->  b2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch, finalKeySearch));
            //LegalFactDownloadMetadataResponse legalFactDownloadMetadataResponse = this.b2bClient.getLegalFact(sharedSteps.getSentNotification().getIun(), categorySearch, finalKeySearch);
        }
        return response;
    }

}
