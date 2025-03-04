package it.pagopa.pn.cucumber.steps.pa.webhookVersions;

import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.ProgressResponseElementV27;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.StreamCreationRequestV27;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.StreamMetadataResponseV27;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.StreamRequestV27;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps;
import it.pagopa.pn.cucumber.steps.pa.WebhookStepsInterface;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;
import java.util.UUID;

import static it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps.StreamVersion.V27;

@Data
public class WebhookStepsV27 implements WebhookStepsInterface {

    private List<StreamCreationRequestV27> streamCreationRequestListV27;
    private List<StreamMetadataResponseV27> eventStreamListV27;
    private StreamRequestV27 streamRequestV27;
    private List<ProgressResponseElementV27> progressResponseElementsV27;
    private AvanzamentoNotificheWebhookB2bSteps webhookSteps;

    public WebhookStepsV27(AvanzamentoNotificheWebhookB2bSteps webhookSteps) {
        this.webhookSteps = webhookSteps;
    }

    public void deleteStreams(String pa) {
        for (StreamMetadataResponseV27 eventStream : eventStreamListV27) {
            webhookSteps.deleteStreamWrapper(V27, pa, eventStream.getStreamId());
        }
    }

    public void initializeStreamRequest(String action, String pa) {
        streamRequestV27 = new StreamRequestV27();
        webhookSteps.initializeRequest(V27, action, pa);
    }

    public void updateStream(UUID idStream) {
        try {
            streamRequestV27 = new StreamRequestV27();
            streamRequestV27.setTitle("Update Stream V27");
            streamRequestV27.setEventType(StreamRequestV27.EventTypeEnum.TIMELINE);
            this.webhookSteps.getWebhookB2bClient().updateEventStreamV27(idStream, streamRequestV27);
        } catch (HttpStatusCodeException e) {
//            this.webhookStepsnotificationError = e;
//            this.webhookSteps.getSharedSteps().setNotificationError(e);
        }
    }

    public void verifyEventNotInStream() {
        Assertions.assertTrue(this.webhookSteps.getWebhookB2bClient().consumeEventStreamV27(this.eventStreamListV27.get(0).getStreamId(), null).isEmpty());
    }
}
