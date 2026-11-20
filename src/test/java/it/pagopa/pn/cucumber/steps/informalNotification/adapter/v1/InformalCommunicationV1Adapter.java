package it.pagopa.pn.cucumber.steps.informalNotification.adapter.v1;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.InformalNotificationRequestV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.NewInformalNotificationResponse;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bExternalInformalClientImpl;
import it.pagopa.pn.cucumber.steps.informalNotification.adapter.InformalApiVersion;
import it.pagopa.pn.cucumber.steps.informalNotification.adapter.InformalCommunicationAdapter;
import it.pagopa.pn.cucumber.steps.informalNotification.adapter.InformalSendContext;
import it.pagopa.pn.cucumber.steps.informalNotification.adapter.InformalSendResult;
import it.pagopa.pn.cucumber.steps.informalNotification.domain.model.InformalCommunication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InformalCommunicationV1Adapter implements InformalCommunicationAdapter {

    private final PnPaB2bExternalInformalClientImpl pnPaB2bExternalInformalClientImpl;

    @Override
    public InformalApiVersion version() {
        return InformalApiVersion.V1;
    }

    @Override
    public InformalSendResult send(InformalSendContext context, InformalCommunication communication) {
        InformalNotificationRequestV1 requestV1 = InformalCommunicationV1Mapper.INSTANCE.toV1(communication);
        // il body JSON effettivamente inviato è loggato dal client (PnPaB2bExternalInformalClientImpl)
        log.info("Sending InformalNotification V1 for cxId={} apiKeyType={}", context.getCxId(), context.getApiKeyType());
        NewInformalNotificationResponse response = pnPaB2bExternalInformalClientImpl.sendNewInformalNotificationV1(requestV1, context.getApiKeyType());

        return InformalSendResult.builder()
                .notificationRequestId(response != null ? response.getNotificationRequestId() : null)
                .rawResponse(response)
                .build();
    }
}
