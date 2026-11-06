package it.pagopa.pn.cucumber.steps.externalChannel;

import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannels.v1.model.PaperEngageRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannels.v1.model.PaperEngageRequestAttachmentsInner;
import it.pagopa.pn.client.b2b.pa.service.IPnExternalChannelsInternalClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class RichiestaCartaceaSteps {

    // L'allegato non viene caricato su SafeStorage: la validazione in ingresso rifiuta la richiesta prima della verifica degli allegati
    private static final String URI_ALLEGATO = "safestorage://PN_NOTIFICATION_ATTACHMENTS-7c1e4b9a2d3f4e5a8b6c0d1e2f3a4b5c.pdf";
    private static final String SHA256_ALLEGATO = "47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=";
    // requestId deve avere almeno 30 caratteri: prefisso + UUID ne garantisce 44
    private static final String PREFISSO_REQUEST_ID = "TEST_QA_";

    private final SharedSteps sharedSteps;
    private final IPnExternalChannelsInternalClient externalChannelsInternalClient;

    @Autowired
    public RichiestaCartaceaSteps(SharedSteps sharedSteps,
                                  IPnExternalChannelsInternalClient externalChannelsInternalClient) {
        this.sharedSteps = sharedSteps;
        this.externalChannelsInternalClient = externalChannelsInternalClient;
    }

    @When("il client {string} invia a external-channel una richiesta di postalizzazione senza requestPaId")
    public void sendPaperEngageRequestWithoutRequestPaId(String clientId) {
        String requestIdx = PREFISSO_REQUEST_ID + UUID.randomUUID();
        PaperEngageRequest paperEngageRequest = buildPaperEngageRequest(requestIdx);

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

    // Tutti i campi obbligatori sono valorizzati e validi, tranne requestPaId che resta assente
    private PaperEngageRequest buildPaperEngageRequest(String requestIdx) {
        PaperEngageRequestAttachmentsInner allegato = new PaperEngageRequestAttachmentsInner()
                .uri(URI_ALLEGATO)
                .order(BigDecimal.ZERO)
                .documentType("ATTO")
                .sha256(SHA256_ALLEGATO);

        return new PaperEngageRequest()
                .requestId(requestIdx)
                .clientRequestTimeStamp(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).toString())
                .productType("AR")
                .attachments(List.of(allegato))
                .printType("BN_FRONTE_RETRO")
                .receiverName("Mario Gherkin")
                .receiverAddress("Via Roma 20")
                .receiverCap("00118")
                .receiverCity("Roma")
                .receiverPr("RM")
                .senderName("PagoPA S.p.A.")
                .senderAddress("Via Sardegna 38")
                .senderCity("Roma")
                .senderPr("RM");
    }
}
