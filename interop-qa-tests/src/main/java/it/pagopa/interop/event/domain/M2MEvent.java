package it.pagopa.interop.event.domain;

import java.time.OffsetDateTime;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/* DEV. NOTE 28/11/2025: una strategia alternativa sarebbe rendere questa classe un wrapper,
 * oltre una semplice astrazione, aggiungendo un campo generico T che si riferisca all'evento
 * originale; in questo scenario, un possibile utilizzo di questa classe sarebbe ad esempio
 * M2MEvent<EServiceEvent> . Non si modificherà comunque l'assetto a meno che non si
 * renda necessario. */
public class M2MEvent {
    @Nonnull    private UUID id;
    @Nonnull    private String eventType;
    @Nonnull    private UUID resourceId;
    @Nullable   private UUID subResourceId;
    @Nonnull    private OffsetDateTime creationTimestamp;
    @Nullable   private UUID producerDelegationId;
    @Nullable   private UUID consumerDelegationId;
}
