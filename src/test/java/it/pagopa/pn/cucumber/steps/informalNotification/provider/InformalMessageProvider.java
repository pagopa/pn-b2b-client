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
                .subject("Oggetto IT")
                .longBody("Test body IT")
                .shortBody("Short IT");

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
