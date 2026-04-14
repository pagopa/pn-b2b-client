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
public class M2MAgreementEvent extends M2MEvent {
    protected UUID producerDelegationId;
    protected UUID consumerDelegationId;
    protected UUID agreementId;
}
