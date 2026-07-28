package it.pagopa.pn.cucumber.steps.informalNotification.provider;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.MessageResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewMessageRequest;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import it.pagopa.pn.cucumber.steps.informalNotification.InformalTemplateMessage.InformalMessageTemplates;
import it.pagopa.pn.cucumber.steps.informalNotification.utils.InformalMessageUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@Component
public class InformalMessageProvider {

    private volatile String messageIdIT;
    private volatile String messageIdITFR;
    private final Object lock = new Object();
    private final PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl;

    public InformalMessageProvider(PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl) {
        this.pnPaB2bInternalInformalClientImpl = pnPaB2bInternalInformalClientImpl;
    }

    // ==========================================
    // IT
    // ==========================================

    public String getOrCreateMessageIT(String cxId) {

        if (messageIdIT == null) {
            synchronized (lock) {
                if (messageIdIT == null) {
                    messageIdIT = createMessageIT(cxId);
                }
            }
        }
        return messageIdIT;
    }

    public String createAndSaveMessageIT(String cxId) {

        String messageId = createMessageIT(cxId);

        verifyMessageCreated(messageId, cxId);

        this.messageIdIT = messageId;

        return messageId;

    }

    public String getSavedMessageIT() {

        if (messageIdIT == null) {
            throw new IllegalStateException("Nessun messaggio IT precedentemente creato");
        }
        return messageIdIT;
    }

    // ==========================================
    // IT-FR
    // ==========================================

    public String getOrCreateMessageITFR(String cxId) {

        if (messageIdITFR == null) {
            synchronized (lock) {
                if (messageIdITFR == null) {
                    messageIdITFR = createMessageITFR(cxId);
                }
            }
        }

        return messageIdITFR;
    }

    public String createAndSaveMessageITFR(String cxId) {

        messageIdITFR = createMessageITFR(cxId);

        return messageIdITFR;
    }

    public String getSavedMessageITFR() {

        if (messageIdITFR == null) {
            throw new IllegalStateException("Nessun messaggio IT-FR precedentemente creato");
        }

        return messageIdITFR;
    }

    // ==========================================
    // CREATION
    // ==========================================

    private String createMessageIT(String cxId) {

        NewMessageRequest request = new NewMessageRequest()
                .primaryMessage(InformalMessageUtils.buildPrimaryMessage(InformalMessageTemplates.SORICAL_IT));

        return pnPaB2bInternalInformalClientImpl.createMessage(cxId, request).getMessageId().toString();
    }

    private String createMessageITFR(String cxId) {

        NewMessageRequest request = new NewMessageRequest()
                .primaryMessage(InformalMessageUtils.buildPrimaryMessage(InformalMessageTemplates.SORICAL_IT))
                .additionalMessage(InformalMessageUtils.buildAdditionalMessage(InformalMessageTemplates.SORICAL_FR));

        return pnPaB2bInternalInformalClientImpl.createMessage(cxId, request).getMessageId().toString();
    }


    private void verifyMessageCreated(String messageId, String cxId) {

        waitForMessageAvailability(UUID.fromString(messageId), cxId);
    }

    private void waitForMessageAvailability(UUID messageId, String cxId) {

        await().atMost(Duration.ofSeconds(10)).pollInterval(Duration.ofSeconds(1)).ignoreExceptions().untilAsserted(() -> {

            MessageResponse response = pnPaB2bInternalInformalClientImpl.getMessage(messageId, cxId);

            assertNotNull(response);
            assertEquals(messageId, response.getMessageId());
        });
    }

}
