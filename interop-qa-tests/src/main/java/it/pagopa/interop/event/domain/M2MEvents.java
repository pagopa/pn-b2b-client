package it.pagopa.interop.event.domain;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* DEV. NOTE 28/11/2025: sebbene al momento non siano previste altre properties oltre gli eventi
 * stessi, si è scelto di prevedere comunque questa classe wrapper qualora in futuro venissero
 * aggiunte altre caratteristiche rilevanti a classi come EServiceEvents. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class M2MEvents {
    private List<M2MEvent> events = new ArrayList<>();

    public M2MEvent getLastEvent() {
        return isEmpty(events) ? null : events.get(events.size() - 1);
    }

    public M2MEvents find(@Nonnull String eventType, @Nonnull UUID resourceId) {
        return find(eventType, resourceId, null);
    }

    public M2MEvents find(@Nonnull String eventType, @Nonnull UUID resourceId, @Nullable UUID subResourceId) {
        Predicate<M2MEvent> eventTypeConsistent = event -> event.getEventType().equals(eventType);
        Predicate<M2MEvent> resourceIdConsistent = event -> event.getResourceId().equals(resourceId);
        Predicate<M2MEvent> subResourceIdConsistent = Objects.nonNull(subResourceId)
            ? event -> event.getSubResourceId().equals(subResourceId)
            : event -> true;
        return new M2MEvents(events.stream()
            .filter(eventTypeConsistent.and(resourceIdConsistent).and(subResourceIdConsistent))
            .toList());
    }
}
