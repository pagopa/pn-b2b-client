package it.pagopa.interop.event.domain.dto;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ProducerKeyEvent;
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
public class M2MProducerKeyEvent extends M2MEvent {
    protected UUID kid;
    protected UUID producerKeychainId;
    protected ProducerKeyEvent.EventTypeEnum eventType;
}
