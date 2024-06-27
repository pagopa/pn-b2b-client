package it.pagopa.pn.cucumber.steps.legalfact;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactCategory;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnWebRecipientClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Component;

@Component
public class LegalFactWebRecipientClient implements LegalFactClient {

    @Override
    public it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.LegalFactDownloadMetadataResponse getLegalFact(
            final SharedSteps sharedSteps,
            final LegalFactCategory categorySearch,
            final String finalKeySearch) {
        IPnWebRecipientClient webRecipientClient = sharedSteps.getWebRecipientClient();
        LegalFactDownloadMetadataResponse response = sharedSteps.deepCopy(getLegalFactDownloadMetadataResponse(sharedSteps, categorySearch, finalKeySearch, webRecipientClient), LegalFactDownloadMetadataResponse.class);
        System.out.println("NOME FILE PEC RECIPIENT DEST: " + response.getFilename());
        return response;
    }

    private static it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.LegalFactDownloadMetadataResponse getLegalFactDownloadMetadataResponse(SharedSteps sharedSteps, LegalFactCategory categorySearch, String finalKeySearch, IPnWebRecipientClient webRecipientClient) {
        return Assertions.assertDoesNotThrow(() ->
                webRecipientClient.getLegalFact(
                        sharedSteps.getSentNotification().getIun(),
                        sharedSteps.deepCopy(categorySearch, it.pagopa.pn.client.web.generated.openapi.clients.externalWebRecipient.model.LegalFactCategory.class),
                        finalKeySearch));
    }
}
