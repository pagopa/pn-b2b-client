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
    private volatile String messageIdMulti;
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

        if (messageIdMulti == null) {
            synchronized (lock) {
                if (messageIdMulti == null) {
                    messageIdMulti = createMessageITFR(cxId);
                }
            }
        }
        return messageIdMulti;
    }

    public String createAndSaveMessageITFR(String cxId) {
        messageIdMulti = createMessageITFR(cxId);
        return messageIdMulti;
    }

    public String getSavedMessageITFR() {

        if (messageIdMulti == null) {
            throw new IllegalStateException("Nessun messaggio IT-FR precedentemente creato");
        }
        return messageIdMulti;
    }

    // ==========================================
    // IT-DE/SL
    // ==========================================

    public String createAndSaveMessageITDE(String cxId) {
        messageIdMulti = createMessageITDE(cxId);
        return messageIdMulti;
    }

    public String createAndSaveMessageITSL(String cxId) {
        messageIdMulti = createMessageITSL(cxId);
        return messageIdMulti;
    }

    public String createAndSaveMessageITEN(String cxId) {
        messageIdMulti = createMessageITEN(cxId);
        return messageIdMulti;
    }

    // ==========================================
    // CREATION
    // ==========================================

    private String createMessageIT(String cxId) {

        NewMessageRequest request = new NewMessageRequest()
                .primaryMessage(InformalMessageUtils.buildPrimaryMessage(InformalMessageTemplates.RISCUOTI_IT.getContent()));

        return pnPaB2bInternalInformalClientImpl.createMessage(cxId, request).getMessageId().toString();
    }

    private String createMessageITFR(String cxId) {

        NewMessageRequest request = new NewMessageRequest()
                .primaryMessage(InformalMessageUtils.buildPrimaryMessage(InformalMessageTemplates.RISCUOTI_IT.getContent()))
                .additionalMessage(InformalMessageUtils.buildAdditionalMessage(InformalMessageTemplates.RISCUOTI_FR.getContent()));

        return pnPaB2bInternalInformalClientImpl.createMessage(cxId, request).getMessageId().toString();
    }

    private String createMessageITDE(String cxId) {

        NewMessageRequest request = new NewMessageRequest()
                .primaryMessage(InformalMessageUtils.buildPrimaryMessage(InformalMessageTemplates.RISCUOTI_IT.getContent()))
                .additionalMessage(InformalMessageUtils.buildAdditionalMessage(InformalMessageTemplates.RISCUOTI_DE.getContent()));

        return pnPaB2bInternalInformalClientImpl.createMessage(cxId, request).getMessageId().toString();
    }

    private String createMessageITSL(String cxId) {

        NewMessageRequest request = new NewMessageRequest()
                .primaryMessage(InformalMessageUtils.buildPrimaryMessage(InformalMessageTemplates.RISCUOTI_IT.getContent()))
                .additionalMessage(InformalMessageUtils.buildAdditionalMessage(InformalMessageTemplates.RISCUOTI_SL.getContent()));

        return pnPaB2bInternalInformalClientImpl.createMessage(cxId, request).getMessageId().toString();
    }

    private String createMessageITEN(String cxId) {

        NewMessageRequest request = new NewMessageRequest()
                .primaryMessage(InformalMessageUtils.buildPrimaryMessage(InformalMessageTemplates.RISCUOTI_IT.getContent()))
                .additionalMessage(InformalMessageUtils.buildAdditionalMessage(InformalMessageTemplates.RISCUOTI_EN.getContent()));

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
