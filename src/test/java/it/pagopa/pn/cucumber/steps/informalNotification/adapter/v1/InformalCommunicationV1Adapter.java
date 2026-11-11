package it.pagopa.pn.cucumber.steps.informalNotification.adapter.v1;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalNotificationRequestV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewInformalNotificationResponse;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import it.pagopa.pn.cucumber.steps.informalNotification.adapter.InformalApiVersion;
import it.pagopa.pn.cucumber.steps.informalNotification.adapter.InformalCommunicationAdapter;
import it.pagopa.pn.cucumber.steps.informalNotification.adapter.InformalSendResult;
import it.pagopa.pn.cucumber.steps.informalNotification.domain.model.InformalCommunication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InformalCommunicationV1Adapter implements InformalCommunicationAdapter {

    private final PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl;

    @Override
    public InformalApiVersion version() {
        return InformalApiVersion.V1;
    }

    @Override
    public InformalSendResult send(String cxId, InformalCommunication communication) {
        InformalNotificationRequestV1 requestV1 = InformalCommunicationV1Mapper.INSTANCE.toV1(communication);
        log.info("Sending InformalNotification V1 payload for cxId={}: {}", cxId, requestV1);
        NewInformalNotificationResponse response = pnPaB2bInternalInformalClientImpl.sendNewInformalNotificationV1(cxId, requestV1);

        return InformalSendResult.builder()
                .notificationRequestId(response != null ? response.getNotificationRequestId() : null)
                .rawResponse(response)
                .build();
    }
}
