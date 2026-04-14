package it.pagopa.interop.event.domain.dto;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AgreementEvent;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class M2MAgreementEvent extends M2MEvent {
    protected UUID producerDelegationId;
    protected UUID consumerDelegationId;
    protected UUID agreementId;
    protected AgreementEvent.EventTypeEnum eventType;
}
