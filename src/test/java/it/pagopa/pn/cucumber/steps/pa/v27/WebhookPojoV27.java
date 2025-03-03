package it.pagopa.pn.cucumber.steps.pa.v27;

import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.ProgressResponseElementV27;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.StreamCreationRequestV27;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.StreamMetadataResponseV27;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.StreamRequestV27;
import it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps;
import lombok.Data;

import java.util.List;

import static it.pagopa.pn.cucumber.steps.pa.AvanzamentoNotificheWebhookB2bSteps.StreamVersion.V27;

@Data
public class WebhookPojoV27 {

    private List<StreamCreationRequestV27> streamCreationRequestListV27;
    private List<StreamMetadataResponseV27> eventStreamListV27;
    private StreamRequestV27 streamRequestV27;
    private List<ProgressResponseElementV27> progressResponseElementsV27;
    private AvanzamentoNotificheWebhookB2bSteps webhookSteps;

    public WebhookPojoV27(AvanzamentoNotificheWebhookB2bSteps webhookSteps) {
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
}
