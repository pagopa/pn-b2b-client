package it.pagopa.interop.event.domain.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode
public class M2MEvent {
    protected UUID id;
    protected Instant eventTimestamp;
    protected String eventType;
}
