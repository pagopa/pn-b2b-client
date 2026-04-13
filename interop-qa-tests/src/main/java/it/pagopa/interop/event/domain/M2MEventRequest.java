package it.pagopa.interop.event.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class M2MEventRequest {
    public static final int EVENTS_MAX_LIMIT = 500;
    @Nullable private UUID lastEventId;
    @Nonnull private int limit = EVENTS_MAX_LIMIT;

    public static M2MEventRequest of(UUID lastEventId) {
        M2MEventRequest m2MEventRequest = new M2MEventRequest();
        m2MEventRequest.setLastEventId(lastEventId);
        return m2MEventRequest;
    }
}
