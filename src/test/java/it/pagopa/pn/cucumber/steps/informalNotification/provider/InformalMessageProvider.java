package it.pagopa.pn.cucumber.steps.informalNotification.provider;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewMessageRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewMessageRequestAdditionalMessage;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewMessageRequestPrimaryMessage;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import org.springframework.stereotype.Component;


@Component
public class InformalMessageProvider {

    private volatile String messageIdIT;
    private volatile String messageIdITFR;
    private final PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl;


    public InformalMessageProvider(PnPaB2bInternalInformalClientImpl client) {
        this.pnPaB2bInternalInformalClientImpl = client;
    }


    private final Object lock = new Object();

    public String getMessageIT(String cxId) {

        if (messageIdIT == null) {
            synchronized (lock) {
                if (messageIdIT == null) {
                    messageIdIT = createMessageIT(cxId);
                }
            }
        }
        return messageIdIT;
    }

    public String getMessageITFR(String cxId) {

        if (messageIdITFR == null) {
            synchronized (lock) {
                if (messageIdITFR == null) {
                    messageIdITFR = createMessageITFR(cxId);
                }
            }
        }
        return messageIdITFR;
    }

    private String createMessageIT(String cxId) {

        NewMessageRequestPrimaryMessage primary = new NewMessageRequestPrimaryMessage()
                .language("IT")
                .subject("È stata emessa una nuova fattura per te")
                .longBody("Sorical S.p.a. ti informa che è stata emessa una fattura per l'utenza n.182140 relativa al periodo 23 dicembre 2025/31 marzo 2026. Di seguito trovi le informazioni principali per il pagamento: Importo:60,68€ Scadenza 26 maggio 2026")
                .shortBody("SEND, il Servizio di Notifiche Digitali, ti informa che hai ricevuto una comunicazione da Sorical S.p.A. Per leggerla, accedi con SPID o CIE al sito di SEND.");

        NewMessageRequest request = new NewMessageRequest()
                .primaryMessage(primary);

        return pnPaB2bInternalInformalClientImpl.createMessage(cxId, request).getMessageId().toString();
    }

    private String createMessageITFR(String cxId) {

        NewMessageRequestPrimaryMessage primary = new NewMessageRequestPrimaryMessage()
                .language("IT")
                .subject("Oggetto IT")
                .longBody("Test body IT")
                .shortBody("Short IT");

        NewMessageRequestAdditionalMessage additional = new NewMessageRequestAdditionalMessage()
                .language("FR")
                .subject("Objet FR")
                .shortBody("Short FR")
                .longBody("Message en français");

        NewMessageRequest request = new NewMessageRequest()
                .primaryMessage(primary)
                .additionalMessage(additional);

        return pnPaB2bInternalInformalClientImpl.createMessage(cxId, request).getMessageId().toString();
    }
}
