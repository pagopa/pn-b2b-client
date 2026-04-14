package it.pagopa.interop.event.domain.dto;

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
public class M2MPurposeTemplateEvent extends M2MEvent {
    protected UUID purposeTemplateId;
    //TODO: decommentare per finalità agevolata
    //protected PurposeTemplateEvent.EventTypeEnum eventType;
}
