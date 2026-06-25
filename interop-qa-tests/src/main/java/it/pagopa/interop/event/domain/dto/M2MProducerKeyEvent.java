package it.pagopa.interop.event.domain.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class M2MProducerKeyEvent extends M2MEvent {
    protected UUID kid;
    protected UUID producerKeychainId;
}
