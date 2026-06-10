package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewMessageRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewMessageRequestAdditionalMessage;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewMessageRequestPrimaryMessage;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("singleton")
public class InformalMessageProvider {

    private volatile String messageIdIT;
    private volatile String messageIdITFR;
    private final PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl;


    public InformalMessageProvider(PnPaB2bInternalInformalClientImpl client) {
        this.pnPaB2bInternalInformalClientImpl = client;
    }


    private final Object lock = new Object();

    public String getMessageIT() {

        if (messageIdIT == null) {
            synchronized (lock) {
                if (messageIdIT == null) {
                    messageIdIT = createMessageIT();
                }
            }
        }

        return messageIdIT;
    }

    public String getMessageITFR() {

        if (messageIdITFR == null) {
            synchronized (lock) {
                if (messageIdITFR == null) {
                    messageIdITFR = createMessageITFR();
                }
            }
        }

        return messageIdITFR;
    }

    private String createMessageIT() {

        NewMessageRequestPrimaryMessage primary = new NewMessageRequestPrimaryMessage()
                .language("IT")
                .subject("Oggetto IT")
                .longBody("Test body IT")
                .shortBody("Short IT");

        NewMessageRequest request = new NewMessageRequest()
                .primaryMessage(primary);

        return pnPaB2bInternalInformalClientImpl.createMessage(request).getMessageId().toString();
    }

    private String createMessageITFR() {

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

        return pnPaB2bInternalInformalClientImpl.createMessage(request).getMessageId().toString();
    }
}
