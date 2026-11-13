package it.pagopa.pn.cucumber.steps.externalChannel;

import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannels.v1.model.PaperEngageRequest;
import it.pagopa.pn.client.b2b.pa.service.IPnExternalChannelsInternalClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.externalChannel.builders.PaperEngageRequestBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class RichiestaCartaceaSteps {

    private final SharedSteps sharedSteps;
    private final IPnExternalChannelsInternalClient externalChannelsInternalClient;
    private final PaperEngageRequestBuilder paperEngageRequestBuilder;

    @Autowired
    public RichiestaCartaceaSteps(SharedSteps sharedSteps,
                                  IPnExternalChannelsInternalClient externalChannelsInternalClient,
                                  PaperEngageRequestBuilder paperEngageRequestBuilder) {
        this.sharedSteps = sharedSteps;
        this.externalChannelsInternalClient = externalChannelsInternalClient;
        this.paperEngageRequestBuilder = paperEngageRequestBuilder;
    }

    @When("il client {string} invia a external-channel una richiesta di postalizzazione senza requestPaId")
    public void sendPaperEngageRequestWithoutRequestPaId(String clientId) {
        PaperEngageRequest paperEngageRequest = paperEngageRequestBuilder.buildWithoutRequestPaId();
        String requestIdx = paperEngageRequest.getRequestId();

        try {
            ResponseEntity<Void> response = externalChannelsInternalClient.sendPaperEngageRequest(requestIdx, clientId, paperEngageRequest);
            log.warn("Richiesta di postalizzazione senza requestPaId accettata da external-channel: requestIdx={}, client={}, status={}",
                    requestIdx, clientId, response.getStatusCode());
        } catch (HttpStatusCodeException e) {
            log.info("Richiesta di postalizzazione senza requestPaId rifiutata: requestIdx={}, client={}, status={}",
                    requestIdx, clientId, e.getStatusCode());
            sharedSteps.setNotificationError(e);
        }
    }
}
