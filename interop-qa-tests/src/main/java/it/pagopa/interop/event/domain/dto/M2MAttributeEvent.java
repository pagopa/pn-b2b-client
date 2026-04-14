package it.pagopa.interop.event.domain.dto;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.AttributeEvent;
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
public class M2MAttributeEvent extends M2MEvent {
    protected UUID attributeId;
    protected AttributeEvent.EventTypeEnum eventType;
}
