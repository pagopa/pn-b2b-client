package it.pagopa.interop.event.domain.dto;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ConsumerDelegationEvent;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ProducerDelegationEvent;
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
public class M2MConsumerDelegationEvent extends M2MEvent {
    protected UUID delegationId;
    protected ConsumerDelegationEvent.EventTypeEnum eventType;
}
