package it.pagopa.pn.cucumber.steps.informalNotification.provider;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.MessageResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.NewMessageRequest;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bExternalInformalClientImpl;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.cucumber.steps.informalNotification.InformalDto.InformalMessageContent;
import it.pagopa.pn.cucumber.steps.informalNotification.InformalTemplateMessage.InformalMessageTemplates;
import it.pagopa.pn.cucumber.steps.informalNotification.utils.InformalMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class InformalMessageProvider {

    private final PnPaB2bExternalInformalClientImpl pnPaB2bExternalInformalClientImpl;

    public InformalMessageProvider(PnPaB2bExternalInformalClientImpl pnPaB2bExternalInformalClientImpl) {
        this.pnPaB2bExternalInformalClientImpl = pnPaB2bExternalInformalClientImpl;
    }

    public String createMessageIT(String cxId, SettableApiKey.ApiKeyType apiKeyType) {
        return createMessage(InformalMessageTemplates.RISCUOTI_IT.getContent(), null, cxId, apiKeyType);
    }

    public String createMessageITFR(String cxId, SettableApiKey.ApiKeyType apiKeyType) {
        return createMessage(InformalMessageTemplates.RISCUOTI_IT.getContent(), InformalMessageTemplates.RISCUOTI_FR.getContent(), cxId, apiKeyType);
    }

    public String createMessageITDE(String cxId, SettableApiKey.ApiKeyType apiKeyType) {
        return createMessage(InformalMessageTemplates.RISCUOTI_IT.getContent(), InformalMessageTemplates.RISCUOTI_DE.getContent(), cxId, apiKeyType);
    }

    public String createMessageITSL(String cxId, SettableApiKey.ApiKeyType apiKeyType) {
        return createMessage(InformalMessageTemplates.RISCUOTI_IT.getContent(), InformalMessageTemplates.RISCUOTI_SL.getContent(), cxId, apiKeyType);
    }

    public String createMessageITEN(String cxId, SettableApiKey.ApiKeyType apiKeyType) {
        return createMessage(InformalMessageTemplates.RISCUOTI_IT.getContent(), InformalMessageTemplates.RISCUOTI_EN.getContent(), cxId, apiKeyType);
    }

    /**
     * Messaggio IT con l'oggetto del template sostituito da {@code subject}.
     * L'oggetto del messaggio è quello che pn-workflow-manager invia su App IO.
     */
    public String createMessageITWithSubject(String subject, String cxId, SettableApiKey.ApiKeyType apiKeyType) {
        NewMessageRequest request = new NewMessageRequest()
                .primaryMessage(InformalMessageUtils.buildPrimaryMessage(InformalMessageTemplates.RISCUOTI_IT.getContent()).subject(subject));
        return createMessage(request, cxId, apiKeyType);
    }

    private String createMessage(InformalMessageContent primaryContent, InformalMessageContent additionalContent, String cxId, SettableApiKey.ApiKeyType apiKeyType) {

        NewMessageRequest request = new NewMessageRequest().primaryMessage(InformalMessageUtils.buildPrimaryMessage(primaryContent));
        if (additionalContent != null) {
            request.additionalMessage(InformalMessageUtils.buildAdditionalMessage(additionalContent));
        }
        return createMessage(request, cxId, apiKeyType);
    }

    private String createMessage(NewMessageRequest request, String cxId, SettableApiKey.ApiKeyType apiKeyType) {
        String messageId = pnPaB2bExternalInformalClientImpl.createMessage(request, apiKeyType).getMessageId().toString();
        verifyMessageCreated(messageId, cxId, apiKeyType);
        return messageId;
    }

    private void verifyMessageCreated(String messageId, String cxId, SettableApiKey.ApiKeyType apiKeyType) {
        waitForMessageAvailability(UUID.fromString(messageId), cxId, apiKeyType);
    }

    private void waitForMessageAvailability(UUID messageId, String cxId, SettableApiKey.ApiKeyType apiKeyType) {

        await().pollInSameThread().atMost(Duration.ofSeconds(10)).pollInterval(Duration.ofSeconds(1)).ignoreExceptions().untilAsserted(() -> {
            MessageResponse response = pnPaB2bExternalInformalClientImpl.getMessage(messageId, apiKeyType);
            assertNotNull(response);
            assertEquals(messageId, response.getMessageId());
        });
    }
}