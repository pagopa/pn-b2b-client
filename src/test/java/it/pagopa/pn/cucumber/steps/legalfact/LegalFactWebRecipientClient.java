package it.pagopa.pn.cucumber.steps.legalfact;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactCategory;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.legalfact.data.LegalFactClientType;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class LegalFactWebRecipientClient implements LegalFactClient{

    private static final LegalFactClientType LEGAL_FACT_WEB_RECIPIENT_CLIENT_TYPE = LegalFactClientType.WEB_RECIPIENT;

    @Override
    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse getLegalFact(
            final List<LegalFactClientType> legalFactClientTypes, final SharedSteps sharedSteps,
            final LegalFactCategory categorySearch, final String finalKeySearch) {
        LegalFactDownloadMetadataResponse response = null;
        if (legalFactClientTypes.contains(LEGAL_FACT_WEB_RECIPIENT_CLIENT_TYPE)) {
            IPnWebRecipientClient webRecipientClient = sharedSteps.getWebRecipientClient();
            response = sharedSteps.deepCopy(getLegalFactDownloadMetadataResponse(sharedSteps, categorySearch, finalKeySearch, webRecipientClient), LegalFactDownloadMetadataResponse.class);
            System.out.println("NOME FILE PEC RECIPIENT DEST: " + response.getFilename());
        }
        return response;
    }

    private static it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.LegalFactDownloadMetadataResponse getLegalFactDownloadMetadataResponse(SharedSteps sharedSteps, LegalFactCategory categorySearch, String finalKeySearch, IPnWebRecipientClient webRecipientClient) {
        return Assertions.assertDoesNotThrow(() ->
                webRecipientClient.getLegalFact(sharedSteps.getSentNotification().getIun(),
                        sharedSteps.deepCopy(categorySearch, it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.LegalFactCategory.class),
                        finalKeySearch));
    }
}
